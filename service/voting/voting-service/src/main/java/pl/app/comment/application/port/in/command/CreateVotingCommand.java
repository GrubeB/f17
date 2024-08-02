package pl.app.comment.application.port.in.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVotingCommand implements
        Serializable {
    private ObjectId idForNewVoting;
    private String domainObjectId;
    private String domainObjectType;
}