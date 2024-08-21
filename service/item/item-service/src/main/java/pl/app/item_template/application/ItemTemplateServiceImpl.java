package pl.app.item_template.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.item_template.application.domain.*;
import pl.app.item_template.application.port.in.ItemTemplateCommand;
import pl.app.item_template.application.port.in.ItemTemplateService;
import pl.app.item_template.query.dto.OutfitTemplateDto;
import pl.app.item_template.query.dto.WeaponTemplateDto;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class ItemTemplateServiceImpl implements ItemTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(ItemTemplateServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<ItemTemplate> createItemTemplate(ItemTemplateCommand.CreateItemTemplateCommand command) {
        logger.debug("creating item template: {}", command.getName());
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(command.getName())), ItemTemplate.class)
                .flatMap(exist -> exist ? Mono.error(ItemTemplateException.DuplicatedNameException.fromName(command.getName())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating item template: {}, exception: {}", command.getName(), e.getMessage()))
                .then(Mono.defer(() -> {
                    ItemTemplate template = new ItemTemplate(command.getType(), command.getName(), command.getDescription(), command.getImageId());
                    var event = new ItemTemplateEvent.ItemTemplateCreatedEvent(
                            template.getId()
                    );
                    return mongoTemplate.insert(template)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getItemTemplateCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created item template: {}, name: {}", saved.getId(), saved.getName());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<OutfitTemplate> createOutfitTemplate(ItemTemplateCommand.CreateOutfitTemplateCommand command) {
        logger.debug("creating outfit template: {}", command.getName());
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(command.getName())), OutfitTemplate.class)
                .flatMap(exist -> exist ? Mono.error(ItemTemplateException.DuplicatedNameException.fromName(command.getName())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating outfit template: {}, exception: {}", command.getName(), e.getMessage()))
                .then(Mono.defer(() -> {
                    OutfitTemplate template = new OutfitTemplate(command.getType(), command.getName(), command.getDescription(), command.getImageId(),
                            command.getPersistence(), command.getDurability(), command.getStrength(), command.getSpeed(), command.getCriticalRate(), command.getCriticalDamage(), command.getAccuracy(), command.getResistance());
                    var event = new ItemTemplateEvent.OutfitTemplateCreatedEvent(
                            template.getId()
                    );
                    return mongoTemplate.insert(template)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getOutfitTemplateCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created outfit template: {}, name: {}", saved.getId(), saved.getName());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<WeaponTemplate> createWeaponTemplate(ItemTemplateCommand.CreateWeaponTemplateCommand command) {
        logger.debug("creating weapon template: {}", command.getName());
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(command.getName())), WeaponTemplate.class)
                .flatMap(exist -> exist ? Mono.error(ItemTemplateException.DuplicatedNameException.fromName(command.getName())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating weapon template: {}, exception: {}", command.getName(), e.getMessage()))
                .then(Mono.defer(() -> {
                    WeaponTemplate template = new WeaponTemplate(command.getType(), command.getName(), command.getDescription(), command.getImageId(),
                            command.getPersistence(), command.getDurability(), command.getStrength(), command.getSpeed(), command.getCriticalRate(), command.getCriticalDamage(), command.getAccuracy(), command.getResistance(),
                            command.getMinDmg(), command.getMaxDmg());
                    var event = new ItemTemplateEvent.WeaponTemplateCreatedEvent(
                            template.getId()
                    );
                    return mongoTemplate.insert(template)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getWeaponTemplateCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created weapon template: {}, name: {}", saved.getId(), saved.getName());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }
}
