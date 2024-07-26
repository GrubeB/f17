package pl.app.comment.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Document(collection = "comments")
@Getter
public class Comment {
    @Id
    private ObjectId id;
    private String content;
    private String userId;
    private CommentStatus status;
    @DocumentReference
    private Set<Comment> comments;

    @DocumentReference
    @JsonIgnore
    private CommentContainer commentContainer;
    @DocumentReference
    @JsonIgnore
    private Comment parentComment;

    public Comment(CommentContainer commentContainer, Comment parentComment, String content, String userId) {
        this.id = new ObjectId();
        this.content = content;
        this.userId = userId;
        this.status = CommentStatus.ACTIVE;
        this.comments = new LinkedHashSet<>();
        this.commentContainer = commentContainer;
        if (Objects.nonNull(parentComment)) {
            this.parentComment = parentComment;
            parentComment.addComment(this);
        }
    }

    public void addComment(Comment newComment) {
        this.comments.add(newComment);
    }
    public Comment addComment(String content, String userId) {
        Comment comment = new Comment(this.commentContainer, this, content, userId);
        this.comments.add(comment);
        return comment;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDeleteStatus() {
        this.status = CommentStatus.DELETED;
    }

    public Optional<Comment> getCommentById(ObjectId id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }
        return this.getAllComments().stream()
                .filter(c -> c.getId().equals(id))
                .findAny();
    }
    @JsonIgnore
    public Set<Comment> getAllComments() {
        return Stream.concat(
                this.comments.stream(),
                this.comments.stream().map(Comment::getAllComments).flatMap(Set::stream)
        ).collect(Collectors.toSet());
    }
}
