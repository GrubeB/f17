package pl.app.army.recruiter.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.army.unit.model.UnitType;

import java.io.Serializable;

public interface RecruiterCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateRecruiterCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddRecruitRequestCommand implements Serializable {
        private ObjectId villageId;
        private UnitType type;
        private Integer amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class StartRecruitRequestCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class FinishRecruitRequestCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CancelRecruitRequestCommand implements Serializable {
        private ObjectId villageId;
        private ObjectId requestId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RejectRecruitRequestCommand implements Serializable {
        private ObjectId villageId;
    }
}
