package pl.app.comment.application.port.in.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentCommand implements
        Serializable {
    private ObjectId commentId;
    private String userId;
    private String content;
}