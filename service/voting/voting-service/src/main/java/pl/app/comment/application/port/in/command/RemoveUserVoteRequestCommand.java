package pl.app.comment.application.port.in.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveUserVoteRequestCommand implements
        Serializable {
    private ObjectId votingId;
    private String domainObjectId;
    private String domainObjectType;

    private String userId;
}