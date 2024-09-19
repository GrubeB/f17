package pl.app.item.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.common.shared.model.ItemType;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.item.application.domain.Item;
import pl.app.item.application.domain.ItemEvent;
import pl.app.item.application.domain.Outfit;
import pl.app.item.application.domain.Weapon;
import pl.app.item.application.port.in.ItemCommand;
import pl.app.item.application.port.in.ItemService;
import pl.app.item.application.port.out.ItemTemplateDomainRepository;
import pl.app.item_template.application.domain.OutfitTemplate;
import pl.app.item_template.application.domain.WeaponTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final ItemTemplateDomainRepository itemTemplateDomainRepository;

    @Override
    public Mono<Item> createItems(ItemCommand.CreateItemCommand command) {
        logger.debug("creating item, based on template: {}", command.getTemplateId());
        return itemTemplateDomainRepository.fetchTemplateById(command.getTemplateId())
                .doOnError(e -> logger.error("exception occurred while creating item, based on template: {}, exception: {}", command.getTemplateId(), e.getMessage()))
                .flatMap(template -> {
                    if (ItemType.isWeapon(template.getType())) {
                        return createWeapon(new ItemCommand.CreateWeaponCommand(command.getTemplateId(), command.getLevel()));
                    } else if (ItemType.isOutfit(template.getType())) {
                        return createOutfit(new ItemCommand.CreateOutfitCommand(command.getTemplateId(), command.getLevel()));
                    }
                    return Mono.error(new RuntimeException());
                });
    }

    @Override
    public Mono<Item> createOutfit(ItemCommand.CreateOutfitCommand command) {
        logger.debug("creating outfit, based on template: {}", command.getTemplateId());
        return itemTemplateDomainRepository.fetchTemplateById(command.getTemplateId())
                .doOnError(e -> logger.error("exception occurred while creating outfit, based on template: {}, exception: {}", command.getTemplateId(), e.getMessage()))
                .flatMap(template -> {
                    Outfit item = new Outfit((OutfitTemplate) template, command.getLevel());
                    var event = new ItemEvent.OutfitCreatedEvent(
                            template.getId()
                    );
                    return mongoTemplate.insert(item)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getOutfitCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created outfit: {}, based on template: {}", saved.getId(), saved.getTemplate().getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Item> createWeapon(ItemCommand.CreateWeaponCommand command) {
        logger.debug("creating weapon, based on template: {}", command.getTemplateId());
        return itemTemplateDomainRepository.fetchTemplateById(command.getTemplateId())
                .doOnError(e -> logger.error("exception occurred while creating weapon, based on template: {}, exception: {}", command.getTemplateId(), e.getMessage()))
                .flatMap(template -> {
                    Weapon item = new Weapon((WeaponTemplate) template, command.getLevel());
                    var event = new ItemEvent.WeaponCreatedEvent(
                            template.getId()
                    );
                    return mongoTemplate.insert(item)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getWeaponCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created weapon: {}, based on template: {}", saved.getId(), saved.getTemplate().getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Flux<Item> createRandomItems(ItemCommand.CreateRandomItemCommand command) {
        logger.debug("creating random items");
        return itemTemplateDomainRepository.fetchRandomTemplate(command.getItemTypes(), command.getNumberOfItems())
                .doOnError(e -> logger.error("exception occurred while creating random items, exception: {}", e.getMessage()))
                .flatMap(template -> {
                    if (ItemType.isWeapon(template.getType())) {
                        return createWeapon(new ItemCommand.CreateWeaponCommand(template.getId(), command.getLevel()));
                    } else if (ItemType.isOutfit(template.getType())) {
                        return createOutfit(new ItemCommand.CreateOutfitCommand(template.getId(), command.getLevel()));
                    } else {
                        return Mono.empty();
                    }
                })
                .doOnComplete(() -> {
                    logger.debug("created random items {}", command.getNumberOfItems());
                });
    }
}
