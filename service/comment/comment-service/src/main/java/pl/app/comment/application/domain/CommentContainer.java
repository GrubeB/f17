package pl.app.comment.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.LinkedHashSet;
import java.util.Set;

@Document(collection = "comments_containers")
@Getter
public class CommentContainer {
    @Id
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    @DocumentReference
    private Set<Comment> comments;

    public CommentContainer(String domainObjectId, String domainObjectType) {
        this.id = new ObjectId();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.comments = new LinkedHashSet<>();
    }

    public void addComment(Comment newComment) {
        this.comments.add(newComment);
    }
}
