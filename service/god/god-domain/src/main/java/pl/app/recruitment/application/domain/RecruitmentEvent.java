package pl.app.recruitment.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;

import java.io.Serializable;

public interface RecruitmentEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class RecruitmentAnnouncementPostedEvent implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }
}
