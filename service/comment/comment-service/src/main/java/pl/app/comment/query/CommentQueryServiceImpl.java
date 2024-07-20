package pl.app.comment.query;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.domain.CommentException;
import pl.app.comment.application.port.out.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
class CommentQueryServiceImpl implements CommentQueryService {
    private final CommentRepository repository;

    @Override
    public List<Comment> fetchAll() {
        return repository.findAll();
    }

    @Override
    public Page<Comment> fetchByPageable(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Comment fetchById(ObjectId id) {
        return repository.findById(id)
                .orElseThrow(() -> CommentException.NotFoundCommentException.fromId(id.toString()));
    }

    @Override
    public List<Comment> fetchByIds(List<ObjectId> ids) {
        return repository.findAllById(ids);
    }
}
