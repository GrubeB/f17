package pl.app.recruitment.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface RecruitmentCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class PostRecruitmentAnnouncementCommand implements Serializable {
        private ObjectId godId;
    }
}
