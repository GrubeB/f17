package pl.app.comment.query.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.comment.application.domain.CommentStatus;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto implements Serializable {
    private ObjectId id;
    private String content;
    private String userId;
    private CommentStatus status;
    private Set<CommentDto> comments;

    private Long numberOfLikes;
    private Long numberOfDislikes;

    @JsonIgnore
    public Set<CommentDto> getAllComments() {
        return Stream.concat(
                this.comments.stream(),
                this.comments.stream().map(CommentDto::getAllComments).flatMap(Set::stream)
        ).collect(Collectors.toSet());
    }
}
