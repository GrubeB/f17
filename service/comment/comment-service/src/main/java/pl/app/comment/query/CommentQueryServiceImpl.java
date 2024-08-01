package pl.app.comment.query;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.app.comment.application.domain.Comment;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class CommentQueryServiceImpl implements CommentQueryService {
    private final CommentRepository repository;

    @Override
    public Mono<Page<Comment>> fetchByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Comment> fetchById(ObjectId id) {
        return repository.findById(id);
    }
}
