package pl.app.monster_template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.CharacterRace;
import pl.app.common.shared.model.Statistics;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonsterTemplateDto implements Serializable {
    private ObjectId id;
    private String name;
    private String description;
    private CharacterRace race;
    private CharacterProfession profession;
    private String imageId;

    private Statistics baseStatistics;
    private Statistics perLevelStatistics;

    private ProgressDto baseProgress;
    private ProgressDto perLevelProgress;

    private MonsterTemplateGearDto gear;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonsterTemplateGearDto implements Serializable {
        protected ObjectId helmetTemplateId;
        protected ObjectId armorTemplateId;
        protected ObjectId glovesTemplateId;
        protected ObjectId bootsTemplateId;
        protected ObjectId beltTemplateId;
        protected ObjectId ringTemplateId;
        protected ObjectId amuletTemplateId;
        protected ObjectId talismanTemplateId;

        protected ObjectId leftHandTemplateId;
        protected ObjectId rightHandTemplateId;
    }

}
