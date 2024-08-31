package pl.app.recruitment.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface RecruitmentResponse {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RecruitmentAnnouncementPostedResponse implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }
}
