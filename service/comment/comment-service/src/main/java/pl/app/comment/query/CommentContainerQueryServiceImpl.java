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
import pl.app.comment.query.dto.CommentContainerDto;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class CommentContainerQueryServiceImpl implements CommentContainerQueryService {
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final CommentContainerRepository repository;
    private final CommentContainerMapper mapper;

    @Override
    public Mono<Page<CommentContainerDto>> fetchByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(mapper::mapCommentContainer)
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }
    @Override
    public Mono<CommentContainerDto> fetchById(ObjectId id) {
        return repository.findById(id)
                .map(mapper::mapCommentContainer);
    }
    @Override
    public Mono<CommentContainerDto> fetchByDomainObject(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        return reactiveMongoTemplate
                .query(CommentContainer.class)
                .matching(query)
                .one()
                .map(mapper::mapCommentContainer);
    }
}
