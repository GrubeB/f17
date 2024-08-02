package pl.app.comment.application.port.in.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVotingRequestCommand implements
        Serializable {
    private String domainObjectId;
    private String domainObjectType;
}