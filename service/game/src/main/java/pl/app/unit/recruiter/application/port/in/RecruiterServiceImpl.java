package pl.app.unit.recruiter.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.building.village_infrastructure.query.VillageInfrastructureDtoQueryService;
import pl.app.building.village_infrastructure.query.dto.VillageInfrastructureDto;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.unit.recruiter.application.domain.Recruiter;
import pl.app.unit.recruiter.application.domain.RecruiterEvent;
import pl.app.unit.recruiter.application.domain.RecruiterException;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.Unit;
import pl.app.unit.unit.application.port.in.UnitDomainRepository;
import pl.app.unit.village_army.application.port.in.VillageArmyCommand;
import pl.app.unit.village_army.application.port.in.VillageArmyService;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
class RecruiterServiceImpl implements RecruiterService {
    private static final Logger logger = LoggerFactory.getLogger(RecruiterServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final RecruiterDomainRepository recruiterDomainRepository;
    private final UnitDomainRepository unitDomainRepository;
    private final VillageArmyService villageArmyService;

    private final VillageResourceService villageResourceService;
    private final VillageInfrastructureDtoQueryService villageInfrastructureDtoQueryService;

    @Override
    public Mono<Recruiter> crate(RecruiterCommand.CreateRecruiterCommand command) {
        return Mono.fromCallable(() ->
                mongoTemplate.exists(Query.query(Criteria.where("villageId").is(command.getVillageId().toHexString())), Recruiter.class)
                        .flatMap(exist -> exist ? Mono.error(RecruiterException.DuplicatedRecruiterException.fromId(command.getVillageId().toHexString())) : Mono.empty())
                        .then(Mono.defer(() -> {
                            var domain = new Recruiter(command.getVillageId(), 7);
                            var event = new RecruiterEvent.RecruiterCreatedEvent(domain.getVillageId());
                            return mongoTemplate.insert(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getRecruiterCreated().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        }))
        ).doOnSubscribe(subscription ->
                logger.debug("crating recruiter for village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created recruiter for village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while crating recruiter for village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<Recruiter> add(RecruiterCommand.AddRecruitRequestCommand command) {
        return Mono.fromCallable(() ->
                Mono.zip(
                                recruiterDomainRepository.fetchByVillageId(command.getVillageId()),
                                villageInfrastructureDtoQueryService.fetchByVillageId(command.getVillageId()),
                                unitDomainRepository.fetch(command.getType())
                        )
                        .flatMap(t -> {
                            var domain = t.getT1();
                            var infrastructure = t.getT2();
                            var unit = t.getT3();
                            verifyVillageMeetsRequirements(infrastructure, unit.getRequirements());
                            var recruitRequest = domain.addRequest(unit, command.getAmount());

                            var event = new RecruiterEvent.RecruitRequestAddedEvent(domain.getVillageId(), recruitRequest.getUnit().getType(),
                                    recruitRequest.getAmount(), recruitRequest.getFrom(), recruitRequest.getTo());
                            return villageResourceService.subtract(new VillageResourceCommand.SubtractResourceCommand(command.getVillageId(), recruitRequest.getCost()))
                                    .then(mongoTemplate.save(domain))
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getRecruitRequestAdded().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("adding recruit request in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("added recruit request in village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while adding recruit request in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    private void verifyVillageMeetsRequirements(VillageInfrastructureDto infrastructure, Set<Unit.Requirement> requirements) {
        var meetRequirements = requirements.stream()
                .allMatch(requirement -> infrastructure.getBuildings().meetRequirements(requirement.getBuildingType(), requirement.getLevel()));
        if (!meetRequirements) {
            throw new RecruiterException.VillageDoseNotMeetRequirementsException();
        }
    }

    @Override
    public Mono<Recruiter> remove(RecruiterCommand.RemoveRecruitRequestCommand command) {
        return Mono.fromCallable(() ->
                recruiterDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            var recruitRequest = domain.removeRequest();
                            if (recruitRequest.isEmpty()) {
                                return Mono.just(domain);
                            }
                            var event = new RecruiterEvent.RecruitRequestRemovedEvent(domain.getVillageId(), recruitRequest.get().getUnit().getType(),
                                    recruitRequest.get().getAmount(), recruitRequest.get().getFrom(), recruitRequest.get().getTo());
                            return villageResourceService.add(new VillageResourceCommand.AddResourceCommand(command.getVillageId(), recruitRequest.get().getCost()))
                                    .then(mongoTemplate.save(domain))
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getRecruitRequestRemoved().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("removing recruit request in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("removed recruit request in village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while removing recruit request in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<Recruiter> finish(RecruiterCommand.FinishRecruitRequestCommand command) {
        return Mono.fromCallable(() ->
                recruiterDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            var recruitRequest = domain.finishRequest();
                            if (recruitRequest.isEmpty()) {
                                return Mono.just(domain);
                            }
                            return villageArmyService.add(new VillageArmyCommand.AddUnitsCommand(domain.getVillageId(), Army.of(Map.of(recruitRequest.get().getUnit().getType(), recruitRequest.get().getAmount()))))
                                    .then(mongoTemplate.save(domain));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("finishing recruit request in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("finished recruit request in village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while finishing recruit request in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

}
