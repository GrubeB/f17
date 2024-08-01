package pl.app.comment.query;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.comment.application.domain.CommentContainer;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class CommentContainerQueryServiceImpl implements CommentContainerQueryService {
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final CommentContainerRepository repository;

    @Override
    public Mono<Page<CommentContainer>> fetchByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<CommentContainer> fetchById(ObjectId id) {
        return repository.findById(id);
    }

    @Override
    public Mono<CommentContainer> fetchByDomainObject(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        return reactiveMongoTemplate.query(CommentContainer.class).matching(query).one();
    }
}
