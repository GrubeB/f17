package pl.app.monster.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.CharacterRace;
import pl.app.common.shared.model.Statistics;
import pl.app.monster_template.application.domain.MonsterTemplate;
import pl.app.common.shared.model.Progress;

@Document(collection = "monster")
@Getter
public class Monster {
    @Id
    private ObjectId id;
    @DocumentReference
    private MonsterTemplate template;
    private Integer level;

    @SuppressWarnings("unused")
    public Monster() {
    }

    public Monster(Integer level, MonsterTemplate template) {
        this.id = ObjectId.get();
        this.template = template;
        this.level = level;
    }

    public Statistics getStatistics() {
        Statistics base = template.getBaseStatistics();
        Statistics perLevel = template.getPerLevelStatistics();
        perLevel.multiply(Long.valueOf(level));
        return base.mergeWith(perLevel);
    }

    public Progress getProgress() {
        Progress base = template.getBaseProgress();
        Progress perLevel = template.getPerLevelProgress();
        perLevel.multiply(Long.valueOf(level));
        return new Progress(base.getExp() + perLevel.getExp());
    }

    public String getName() {
        return this.template.getName();
    }

    public String getDescription() {
        return this.template.getDescription();
    }

    public CharacterRace getRace() {
        return this.template.getRace();
    }

    public CharacterProfession getProfession() {
        return this.template.getProfession();
    }

    public String getImageId() {
        return this.template.getImageId();
    }
}
