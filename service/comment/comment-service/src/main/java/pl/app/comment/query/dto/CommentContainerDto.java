package pl.app.comment.query.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentContainerDto implements Serializable {
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    private Set<CommentDto> comments;

    public Optional<CommentDto> getCommentById(ObjectId id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }
        return this.getAllComments().stream()
                .filter(c -> c.getId().equals(id))
                .findAny();
    }

    @JsonIgnore
    public Set<CommentDto> getAllComments() {
        return Stream.concat(
                this.comments.stream(),
                this.comments.stream().map(CommentDto::getAllComments).flatMap(Set::stream)
        ).collect(Collectors.toSet());
    }
}
