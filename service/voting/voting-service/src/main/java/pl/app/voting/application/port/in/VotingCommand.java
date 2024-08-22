package pl.app.voting.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface VotingCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddUserVoteCommand implements Serializable {
        private ObjectId votingId;
        private String domainObjectId;
        private String domainObjectType;

        private String userId;
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddUserVoteRequestCommand implements Serializable {
        private ObjectId votingId;
        private String domainObjectId;
        private String domainObjectType;

        private String userId;
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateVotingCommand implements Serializable {
        private ObjectId idForNewVoting;
        private String domainObjectId;
        private String domainObjectType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateVotingRequestCommand implements Serializable {
        private String domainObjectId;
        private String domainObjectType;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveUserVoteCommand implements Serializable {
        private ObjectId votingId;
        private String domainObjectId;
        private String domainObjectType;

        private String userId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveUserVoteRequestCommand implements Serializable {
        private ObjectId votingId;
        private String domainObjectId;
        private String domainObjectType;

        private String userId;
    }
}