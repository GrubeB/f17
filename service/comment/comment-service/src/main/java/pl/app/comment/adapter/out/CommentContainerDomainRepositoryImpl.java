package pl.app.comment.adapter.out;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.stereotype.Component;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.application.domain.CommentException;
import pl.app.comment.application.port.out.CommentContainerDomainRepository;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Function;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
class CommentContainerDomainRepositoryImpl implements CommentContainerDomainRepository {
    private static final Logger logger = LoggerFactory.getLogger(CommentContainerDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final CommentContainerRepository repository;

    public CommentContainerDomainRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(CommentContainerRepository.class);
    }

    @Component
    public interface CommentContainerRepository extends ReactiveMongoRepository<CommentContainer, ObjectId> {
    }

    @Override
    public Mono<CommentContainer> fetchById(ObjectId containerId) {
        Query query = Query.query(Criteria.where("_id").is(containerId));
        return mongoTemplate.query(CommentContainer.class).matching(query).one()
                .doOnNext(commentContainer -> logger.debug("Fetched comment container with id: {}, for domain object: {} of type: {}", commentContainer.getId(), commentContainer.getDomainObjectId(), commentContainer.getDomainObjectType()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> CommentException.NotFoundCommentContainerException.fromId(containerId.toString()))))
                .doOnError(e -> logger.error("Error occurred while fetching comment container with id: {}: {}", containerId, e.toString()));
    }

    @Override
    public Mono<CommentContainer> fetchByCommentId(ObjectId commentId) {
        Query query = Query.query(where("id").is(commentId));
        return mongoTemplate.query(Comment.class).matching(query).one()
                .doOnNext(comment -> logger.debug("Fetched comment with id: {}", comment.getId()))
                .map(Comment::getCommentContainer)
                .switchIfEmpty(Mono.error(() -> CommentException.NotFoundCommentException.fromId(commentId.toString())))
                .doOnError(e -> logger.error("Error occurred while fetching container with id: {}: {}", commentId, e.toString()));
    }

    @Override
    public Mono<CommentContainer> fetchByDomainObject(String domainObjectId, String domainObjectType) {
        Query query = Query.query(where("domainObjectId").is(domainObjectId).and("domainObjectType").is(domainObjectType));
        return mongoTemplate.query(CommentContainer.class).matching(query).one()
                .doOnNext(commentContainer -> logger.debug("Fetched comment container with id: {}, for domain object: {} of type: {}", commentContainer.getId(), commentContainer.getDomainObjectId(), commentContainer.getDomainObjectType()))
                .switchIfEmpty(Mono.error(() -> CommentException.NotFoundCommentContainerException.fromDomainObject(domainObjectId, domainObjectType)))
                .doOnError(e -> logger.error("Error occurred while fetching comment container for domain object: {} of type: {}: {}", domainObjectId, domainObjectType, e.toString()));
    }

    @Override
    public Mono<CommentContainer> fetchByIdOrDomainObject(ObjectId id, String domainObjectId, String domainObjectType) {
        if (Objects.nonNull(id)) {
            return fetchById(id);
        } else if (Objects.nonNull(domainObjectId) && Objects.nonNull(domainObjectType)) {
            return fetchByDomainObject(domainObjectId, domainObjectType);
        }
        return Mono.error(CommentException.NotFoundCommentContainerException::new);
    }
}
