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
import pl.app.item_template.application.domain.ItemTemplateEvent;
import pl.app.item_template.application.domain.ItemTemplateException;
import pl.app.item_template.application.domain.OutfitTemplate;
import pl.app.item_template.application.domain.WeaponTemplate;
import pl.app.item_template.application.port.in.ItemTemplateCommand;
import pl.app.item_template.application.port.in.ItemTemplateService;
import pl.app.item_template.application.port.in.ItemTemplateDomainRepository;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class ItemTemplateServiceImpl implements ItemTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(ItemTemplateServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final ItemTemplateDomainRepository domainRepository;

    @Override
    public Mono<OutfitTemplate> createOutfitTemplate(ItemTemplateCommand.CreateOutfitTemplateCommand command) {
        logger.debug("creating outfit template: {}", command.getName());
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(command.getName())), OutfitTemplate.class)
                .flatMap(exist -> exist ? Mono.error(ItemTemplateException.DuplicatedNameException.fromName(command.getName())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating outfit template: {}, exception: {}", command.getName(), e.getMessage()))
                .then(Mono.defer(() -> {
                    OutfitTemplate template = innerCreateOutfitTemplate(command);
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

    private OutfitTemplate innerCreateOutfitTemplate(ItemTemplateCommand.CreateOutfitTemplateCommand command) {
        return new OutfitTemplate(command.getType(), command.getName(), command.getDescription(), command.getImageId(),
                command.getMoney(), command.getMoneyPercentage(),
                command.getPersistence(), command.getPersistencePercentage(),
                command.getDurability(), command.getDurabilityPercentage(),
                command.getStrength(), command.getStrengthPercentage(),
                command.getSpeed(), command.getSpeedPercentage(),
                command.getCriticalRate(), command.getCriticalRatePercentage(),
                command.getCriticalDamage(), command.getCriticalDamagePercentage(),
                command.getAccuracy(), command.getAccuracyPercentage(),
                command.getResistance(), command.getResistancePercentage()
        );
    }

    @Override
    public Mono<OutfitTemplate> updateOutfitTemplate(ItemTemplateCommand.UpdateOutfitTemplateCommand command) {
        logger.debug("updating outfit template: {}", command.getId());
        return domainRepository.fetchOutfitTemplateById(command.getId())
                .doOnError(e -> logger.error("exception occurred while updating outfit template: {}, exception: {}", command.getId(), e.getMessage()))
                .flatMap(domain ->{
                    innerUpdateOutfitTemplate(domain, command);
                    var event = new ItemTemplateEvent.OutfitTemplateUpdatedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getOutfitTemplateUpdated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("updated outfit template: {}, name: {}", saved.getId(), saved.getName());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    private void innerUpdateOutfitTemplate(OutfitTemplate template, ItemTemplateCommand.UpdateOutfitTemplateCommand command) {
        template.setType(command.getType());
        template.setName(command.getName());
        template.setDescription(command.getDescription());
        template.setImageId(command.getImageId());
        template.setMoney(command.getMoney());
        template.setMoneyPercentage(command.getMoneyPercentage());
        template.setPersistence(command.getPersistence());
        template.setPersistencePercentage(command.getPersistencePercentage());
        template.setDurability(command.getDurability());
        template.setDurabilityPercentage(command.getDurabilityPercentage());
        template.setStrength(command.getStrength());
        template.setStrengthPercentage(command.getStrengthPercentage());
        template.setSpeed(command.getSpeed());
        template.setSpeedPercentage(command.getSpeedPercentage());
        template.setCriticalRate(command.getCriticalRate());
        template.setCriticalRatePercentage(command.getCriticalRatePercentage());
        template.setCriticalDamage(command.getCriticalDamage());
        template.setCriticalDamagePercentage(command.getCriticalDamagePercentage());
        template.setAccuracy(command.getAccuracy());
        template.setAccuracyPercentage(command.getAccuracyPercentage());
        template.setResistance(command.getResistance());
        template.setResistancePercentage(command.getResistancePercentage());
    }

    @Override
    public Mono<OutfitTemplate> deleteOutfitTemplate(ItemTemplateCommand.DeleteOutfitTemplateCommand command) {
        logger.debug("deleting outfit template: {}", command.getId());
        return domainRepository.fetchOutfitTemplateById(command.getId())
                .doOnError(e -> logger.error("exception occurred while deleting outfit template: {}, exception: {}", command.getId(), e.getMessage()))
                .flatMap(domain ->{
                    var event = new ItemTemplateEvent.OutfitTemplateDeletedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.remove(domain).thenReturn(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getOutfitTemplateDeleted().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("deleted outfit template: {}, name: {}", saved.getId(), saved.getName());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }


    @Override
    public Mono<WeaponTemplate> createWeaponTemplate(ItemTemplateCommand.CreateWeaponTemplateCommand command) {
        logger.debug("creating weapon template: {}", command.getName());
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(command.getName())), WeaponTemplate.class)
                .flatMap(exist -> exist ? Mono.error(ItemTemplateException.DuplicatedNameException.fromName(command.getName())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating weapon template: {}, exception: {}", command.getName(), e.getMessage()))
                .then(Mono.defer(() -> {
                    WeaponTemplate template = innerCreateWeaponTemplate(command);
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

    private WeaponTemplate innerCreateWeaponTemplate(ItemTemplateCommand.CreateWeaponTemplateCommand command) {
        return new WeaponTemplate(command.getType(), command.getName(), command.getDescription(), command.getImageId(),
                command.getMoney(), command.getMoneyPercentage(),
                command.getPersistence(), command.getPersistencePercentage(),
                command.getDurability(), command.getDurabilityPercentage(),
                command.getStrength(), command.getStrengthPercentage(),
                command.getSpeed(), command.getSpeedPercentage(),
                command.getCriticalRate(), command.getCriticalRatePercentage(),
                command.getCriticalDamage(), command.getCriticalDamagePercentage(),
                command.getAccuracy(), command.getAccuracyPercentage(),
                command.getResistance(), command.getResistancePercentage(),
                command.getMinDmg(), command.getMinDmgPercentage(),
                command.getMaxDmg(), command.getMaxDmgPercentage()
        );
    }

    @Override
    public Mono<WeaponTemplate> updateWeaponTemplate(ItemTemplateCommand.UpdateWeaponTemplateCommand command) {
        logger.debug("updating weapon template: {}", command.getId());
        return domainRepository.fetchWeaponTemplateById(command.getId())
                .doOnError(e -> logger.error("exception occurred while updating weapon template: {}, exception: {}", command.getId(), e.getMessage()))
                .flatMap(domain ->{
                    innerUpdateWeaponTemplate(domain, command);
                    var event = new ItemTemplateEvent.WeaponTemplateUpdatedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getWeaponTemplateUpdated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("updated weapon template: {}, name: {}", saved.getId(), saved.getName());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    private void innerUpdateWeaponTemplate(WeaponTemplate template, ItemTemplateCommand.UpdateWeaponTemplateCommand command) {
        template.setType(command.getType());
        template.setName(command.getName());
        template.setDescription(command.getDescription());
        template.setImageId(command.getImageId());
        template.setMoney(command.getMoney());
        template.setMoneyPercentage(command.getMoneyPercentage());
        template.setPersistence(command.getPersistence());
        template.setPersistencePercentage(command.getPersistencePercentage());
        template.setDurability(command.getDurability());
        template.setDurabilityPercentage(command.getDurabilityPercentage());
        template.setStrength(command.getStrength());
        template.setStrengthPercentage(command.getStrengthPercentage());
        template.setSpeed(command.getSpeed());
        template.setSpeedPercentage(command.getSpeedPercentage());
        template.setCriticalRate(command.getCriticalRate());
        template.setCriticalRatePercentage(command.getCriticalRatePercentage());
        template.setCriticalDamage(command.getCriticalDamage());
        template.setCriticalDamagePercentage(command.getCriticalDamagePercentage());
        template.setAccuracy(command.getAccuracy());
        template.setAccuracyPercentage(command.getAccuracyPercentage());
        template.setResistance(command.getResistance());
        template.setResistancePercentage(command.getResistancePercentage());
        template.setMinDmg(command.getMinDmg());
        template.setMinDmgPercentage(command.getMinDmgPercentage());
        template.setMaxDmg(command.getMaxDmg());
        template.setMaxDmgPercentage(command.getMaxDmgPercentage());
    }
    @Override
    public Mono<WeaponTemplate> deleteWeaponTemplate(ItemTemplateCommand.DeleteWeaponTemplateCommand command) {
        logger.debug("deleting weapon template: {}", command.getId());
        return domainRepository.fetchWeaponTemplateById(command.getId())
                .doOnError(e -> logger.error("exception occurred while deleting weapon template: {}, exception: {}", command.getId(), e.getMessage()))
                .flatMap(domain ->{
                    var event = new ItemTemplateEvent.WeaponTemplateDeletedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.remove(domain).thenReturn(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getWeaponTemplateDeleted().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("deleted weapon template: {}, name: {}", saved.getId(), saved.getName());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
