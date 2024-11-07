package pl.app.tribe.application.port.in;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.tribe.application.domain.Tribe;
import pl.app.tribe.application.domain.TribeException;
import reactor.core.publisher.Mono;

import java.util.function.Function;


@Service
@RequiredArgsConstructor
class TribeServiceImpl implements TribeService {
    private static final Logger logger = LoggerFactory.getLogger(TribeServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    private final TribeDomainRepository tribeDomainRepository;

    @Override
    public Mono<Tribe> create(TribeCommand.CreateTribeCommand command) {
        return Mono.fromCallable(() ->
                mongoTemplate.exists(Query.query(Criteria.where("name").is(command.getName())), Tribe.class)
                        .flatMap(exist -> exist ? Mono.error(TribeException.DuplicatedNameException.fromName(command.getName())) : Mono.empty())
                        .then(Mono.defer(() -> {
                            var domain = new Tribe(command.getName(), command.getAbbreviation());
                            domain.addMember(command.getFounderId());
                            domain.changeMemberType(command.getFounderId(), Tribe.MemberType.FOUNDER);
                            return mongoTemplate.insert(domain);
                        }))
        ).doOnSubscribe(subscription ->
                logger.debug("creating tribe with name: {}", command.getName())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created tribe: {}", domain.getId())
        ).doOnError(e ->
                logger.error("exception occurred while creating tribe with name: {}, exception: {}", command.getName(), e.toString())
        );
    }

    @Override
    public Mono<Tribe> addMember(TribeCommand.AddMemberCommand command) {
        return Mono.fromCallable(() ->
                tribeDomainRepository.fetchById(command.getTribeId())
                        .flatMap(domain -> {
                            domain.addMember(command.getMemberId());
                            return mongoTemplate.insert(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("adding member to tribe: {}", command.getTribeId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("added member to tribe: {}", domain.getId())
        ).doOnError(e ->
                logger.error("exception occurred while adding member to tribe: {}, exception: {}", command.getTribeId(), e.toString())
        );
    }

    @Override
    public Mono<Tribe> removeMember(TribeCommand.RemoveMemberCommand command) {
        return Mono.fromCallable(() ->
                tribeDomainRepository.fetchById(command.getTribeId())
                        .flatMap(domain -> {
                            domain.removeMember(command.getMemberId());
                            return mongoTemplate.insert(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("removing member from tribe: {}", command.getTribeId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("removed member from tribe: {}", domain.getId())
        ).doOnError(e ->
                logger.error("exception occurred while removing member from tribe: {}, exception: {}", command.getTribeId(), e.toString())
        );
    }

    @Override
    public Mono<Tribe> changeMemberType(TribeCommand.ChangeMemberTypeCommand command) {
        return Mono.fromCallable(() ->
                tribeDomainRepository.fetchById(command.getTribeId())
                        .flatMap(domain -> {
                            domain.changeMemberType(command.getMemberId(), command.getType());
                            return mongoTemplate.insert(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("changing member type in tribe: {}", command.getTribeId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("changed member type in tribe: {}", domain.getId())
        ).doOnError(e ->
                logger.error("exception occurred while changing member type in tribe: {}, exception: {}", command.getTribeId(), e.toString())
        );
    }

    @Override
    public Mono<Tribe> addDiplomacy(TribeCommand.AddDiplomacyCommand command) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Mono<Tribe> removeDiplomacy(TribeCommand.RemoveDiplomacyCommand command) {
        throw new RuntimeException("Not implemented yet");
    }
}
