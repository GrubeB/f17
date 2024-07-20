package pl.app.comment;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Document(collection = "comments")
@Getter
public class Comment {
    @Id
    private ObjectId id;
    private String content;
    private String userId;
    @DocumentReference
    private Set<Comment> comments;

    public Comment(String content, String userId) {
        this.id = new ObjectId();
        this.content = content;
        this.userId = userId;
        this.comments = new LinkedHashSet<>();
    }

    public void addComment(Comment newComment) {
        this.comments.add(newComment);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
