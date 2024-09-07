package pl.app.monster_template.application;

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
import pl.app.gear.aplication.domain.TemplateGear;
import pl.app.monster_template.application.domain.MonsterTemplate;
import pl.app.monster_template.application.domain.MonsterTemplateEvent;
import pl.app.monster_template.application.domain.MonsterTemplateException;
import pl.app.monster_template.application.port.in.MonsterTemplateService;
import pl.app.monster_template.application.port.in.MonsterTemplateDomainRepository;
import pl.app.monster_template.in.MonsterTemplateCommand;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class MonsterTemplateServiceImpl implements MonsterTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(MonsterTemplateServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final MonsterTemplateDomainRepository monsterTemplateDomainRepository;


    @Override
    public Mono<MonsterTemplate> create(MonsterTemplateCommand.CreateMonsterTemplateCommand command) {
        logger.debug("creating monster template");
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(command.getName())), MonsterTemplate.class)
                .flatMap(exist -> exist ? Mono.error(MonsterTemplateException.DuplicatedNameException.fromName(command.getName())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating monster template, exception: {}", e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new MonsterTemplate(
                            command.getName(),
                            command.getDescription(),
                            command.getRace(),
                            command.getProfession(),
                            command.getImageId(),
                            command.getBase(),
                            command.getPerLevel(),
                            new TemplateGear(
                                    command.getHelmetTemplateId(),
                                    command.getArmorTemplateId(),
                                    command.getGlovesTemplateId(),
                                    command.getBootsTemplateId(),
                                    command.getBeltTemplateId(),
                                    command.getRingTemplateId(),
                                    command.getAmuletTemplateId(),
                                    command.getTalismanTemplateId(),
                                    command.getLeftHandTemplateId(),
                                    command.getRightHandTemplateId()
                            )
                    );
                    var event = new MonsterTemplateEvent.MonsterTemplateCreatedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMonsterTemplateCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created monster template {}", saved.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<MonsterTemplate> update(MonsterTemplateCommand.UpdateMonsterTemplateCommand command) {
        logger.debug("updating character template {}", command.getId());
        return monsterTemplateDomainRepository.fetchById(command.getId())
                .doOnError(e -> logger.error("exception occurred while updating character template {} , exception: {}", command.getId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.setName(command.getName());
                    domain.setDescription(command.getDescription());
                    domain.setRace(command.getRace());
                    domain.setProfession(command.getProfession());
                    domain.setImageId(command.getImageId());

                    domain.setBase(command.getBase());
                    domain.setPerLevel(command.getPerLevel());
                    domain.getGear().setHelmetTemplateId(command.getHelmetTemplateId());
                    domain.getGear().setArmorTemplateId(command.getArmorTemplateId());
                    domain.getGear().setGlovesTemplateId(command.getGlovesTemplateId());
                    domain.getGear().setBootsTemplateId(command.getBootsTemplateId());
                    domain.getGear().setBeltTemplateId(command.getBeltTemplateId());
                    domain.getGear().setRingTemplateId(command.getRingTemplateId());
                    domain.getGear().setAmuletTemplateId(command.getAmuletTemplateId());
                    domain.getGear().setTalismanTemplateId(command.getTalismanTemplateId());
                    domain.getGear().setLeftHandTemplateId(command.getLeftHandTemplateId());
                    domain.getGear().setRightHandTemplateId(command.getRightHandTemplateId());

                    var event = new MonsterTemplateEvent.MonsterTemplateUpdatedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMonsterTemplateUpdated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("updated monster template {}", saved.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<MonsterTemplate> remove(MonsterTemplateCommand.RemoveMonsterTemplateCommand command) {
        logger.debug("deleting monster template {}", command.getId());
        return monsterTemplateDomainRepository.fetchById(command.getId())
                .doOnError(e -> logger.error("exception occurred while deleting monster template {} , exception: {}", command.getId(), e.getMessage()))
                .flatMap(domain -> {
                    var event = new MonsterTemplateEvent.MonsterTemplateDeletedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.remove(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMonsterTemplateRemoved().getName(), domain.getId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> {
                                logger.debug("deleted monster template {}", domain.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
