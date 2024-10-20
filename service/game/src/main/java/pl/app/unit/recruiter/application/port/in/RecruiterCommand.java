package pl.app.unit.recruiter.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.unit.unit.application.domain.UnitType;

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
    class RemoveRecruitRequestCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class FinishRecruitRequestCommand implements Serializable {
        private ObjectId villageId;
    }
}
