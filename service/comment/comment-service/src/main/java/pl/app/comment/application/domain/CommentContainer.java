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

@Document(collection = "comments_containers")
@Getter
public class CommentContainer {
    @Id
    private final ObjectId id;
    private final String domainObjectType;
    private final String domainObjectId;
    @DocumentReference
    private final Set<Comment> comments;

    public CommentContainer(String domainObjectId, String domainObjectType) {
        this.id = new ObjectId();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.comments = new LinkedHashSet<>();
    }

    public Comment addComment(String content, String userId) {
        Comment comment = new Comment(this, null, content, userId);
        this.comments.add(comment);
        return comment;
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
