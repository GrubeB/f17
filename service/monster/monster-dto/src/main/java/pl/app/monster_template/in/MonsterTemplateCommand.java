package pl.app.monster_template.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.CharacterRace;
import pl.app.common.shared.model.Progress;
import pl.app.common.shared.model.Statistics;
import pl.app.monster_template.dto.ProgressDto;

import java.io.Serializable;


public interface MonsterTemplateCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateMonsterTemplateCommand implements Serializable {
        private String name;
        private String description;
        private CharacterRace race;
        private CharacterProfession profession;
        private String imageId;

        private Statistics baseStatistics;
        private Statistics perLevelStatistics;

        private Progress baseProgress;
        private Progress perLevelProgress;

        private ObjectId helmetTemplateId;
        private ObjectId armorTemplateId;
        private ObjectId glovesTemplateId;
        private ObjectId bootsTemplateId;
        private ObjectId beltTemplateId;
        private ObjectId ringTemplateId;
        private ObjectId amuletTemplateId;
        private ObjectId talismanTemplateId;

        private ObjectId leftHandTemplateId;
        private ObjectId rightHandTemplateId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UpdateMonsterTemplateCommand implements Serializable {
        private ObjectId id;
        private String name;
        private String description;
        private CharacterRace race;
        private CharacterProfession profession;
        private String imageId;

        private Statistics baseStatistics;
        private Statistics perLevelStatistics;

        private Progress baseProgress;
        private Progress perLevelProgress;

        private ObjectId helmetTemplateId;
        private ObjectId armorTemplateId;
        private ObjectId glovesTemplateId;
        private ObjectId bootsTemplateId;
        private ObjectId beltTemplateId;
        private ObjectId ringTemplateId;
        private ObjectId amuletTemplateId;
        private ObjectId talismanTemplateId;

        private ObjectId leftHandTemplateId;
        private ObjectId rightHandTemplateId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveMonsterTemplateCommand implements Serializable {
        private ObjectId id;
    }
}
