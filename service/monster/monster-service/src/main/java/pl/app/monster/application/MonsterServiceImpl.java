package pl.app.monster.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.monster.application.domain.Monster;
import pl.app.monster.application.domain.MonsterEvent;
import pl.app.monster.application.port.in.MonsterCommand;
import pl.app.monster.application.port.in.MonsterDomainRepository;
import pl.app.monster.application.port.in.MonsterService;
import pl.app.monster_template.application.port.in.MonsterTemplateDomainRepository;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class MonsterServiceImpl implements MonsterService {
    private static final Logger logger = LoggerFactory.getLogger(MonsterServiceImpl.class);

    private final MonsterDomainRepository domainRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final MonsterTemplateDomainRepository monsterTemplateDomainRepository;

    @Override
    public Mono<Monster> create(MonsterCommand.CreateMonsterCommand command) {
        logger.debug("creating monster: {}", command.getTemplateId());
        return monsterTemplateDomainRepository.fetchById(command.getTemplateId())
                .doOnError(e -> logger.error("exception occurred while creating monster: {}, exception: {}", command.getTemplateId(), e.getMessage()))
                .flatMap(template -> {
                    var domain = new Monster(command.getLevel(), template);
                    var event = new MonsterEvent.MonsterCreatedEvent(
                            domain.getId(),
                            domain.getLevel(),
                            domain.getTemplate().getGear().getHelmetTemplateId(),
                            domain.getTemplate().getGear().getArmorTemplateId(),
                            domain.getTemplate().getGear().getGlovesTemplateId(),
                            domain.getTemplate().getGear().getBootsTemplateId(),
                            domain.getTemplate().getGear().getBeltTemplateId(),
                            domain.getTemplate().getGear().getRingTemplateId(),
                            domain.getTemplate().getGear().getAmuletTemplateId(),
                            domain.getTemplate().getGear().getTalismanTemplateId(),
                            domain.getTemplate().getGear().getLeftHandTemplateId(),
                            domain.getTemplate().getGear().getRightHandTemplateId()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMonsterCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created monster: {}", saved.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }


    @Override
    public Mono<Monster> remove(MonsterCommand.RemoveMonsterCommand command) {
        logger.debug("removing monster: {}", command.getMonsterId());
        return domainRepository.fetchById(command.getMonsterId())
                .doOnError(e -> logger.error("exception occurred while removing monster: {}, exception: {}", command.getMonsterId(), e.getMessage()))
                .flatMap(domain -> {
                    var event = new MonsterEvent.MonsterRemovedEvent(
                            domain.getId()
                    );
                    return mongoTemplate.remove(domain)
                            .then(Mono.defer(() -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMonsterRemoved().getName(), domain.getId(), event)).thenReturn(domain)))
                            .doOnSuccess(removed -> {
                                logger.debug("removed monster: {}", removed.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
