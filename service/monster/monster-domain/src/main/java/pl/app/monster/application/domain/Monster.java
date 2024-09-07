package pl.app.monster.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.common.shared.model.Statistics;
import pl.app.monster_template.application.domain.MonsterTemplate;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.CharacterRace;

@Document(collection = "monster")
@Getter
public class Monster {
    @Id
    private ObjectId id;
    @DocumentReference
    private MonsterTemplate template;
    private Statistics statistics;
    private Integer level;

    @SuppressWarnings("unused")
    public Monster() {
    }

    public Monster(Integer level, MonsterTemplate template) {
        this.id = ObjectId.get();
        this.template = template;
        this.level = level;
        this.statistics = calculateStatistics(level, template);
    }
    private Statistics calculateStatistics(Integer level, MonsterTemplate template){
        Statistics base = template.getBase();
        Statistics perLevel = template.getPerLevel();
        perLevel.multiply(Long.valueOf(level));
        return base.mergeWith(perLevel);
    }

    public String getName(){
        return this.template.getName();
    }
    public String getDescription(){
        return this.template.getDescription();
    }
    public CharacterRace getRace(){
        return this.template.getRace();
    }
    public CharacterProfession getProfession(){
        return this.template.getProfession();
    }
    public String getImageId(){
        return this.template.getImageId();
    }
}
