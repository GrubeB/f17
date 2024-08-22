package pl.app.voting.query;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.voting.application.domain.Voting;
import pl.app.voting.query.dto.VotingDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
class VotingQueryServiceImpl implements VotingQueryService {
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final VotingRepository repository;
    private final VotingMapper mapper;

    @Override
    public Mono<Page<VotingDto>> fetchByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, VotingDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<VotingDto> fetchById(ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, VotingDto.class));
    }

    @Override
    public Mono<VotingDto> fetchByDomainObject(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectType").is(domainObjectType)
                .and("domainObjectId").is(domainObjectId)
        );
        return reactiveMongoTemplate.query(Voting.class).matching(query).one()
                .map(e -> mapper.map(e, VotingDto.class));
    }

    @Override
    public Flux<VotingDto> fetchByDomainObject(List<String> domainObjectIds, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectType").is(domainObjectType)
                .and("domainObjectId").in(domainObjectIds)
        );
        return reactiveMongoTemplate.query(Voting.class).matching(query).all()
                .map(e -> mapper.map(e, VotingDto.class));
    }
}
