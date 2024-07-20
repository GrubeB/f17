package pl.app.comment;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Document(collection = "comments_containers")
@Getter
public class CommentContainer {
    @Id
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    @DocumentReference
    private Set<Comment> comments;

    public CommentContainer(String domainObjectType, String domainObjectId) {
        this.id = new ObjectId();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.comments = new LinkedHashSet<>();
    }

    public void addComment(Comment newComment) {
        this.comments.add(newComment);
    }
}
