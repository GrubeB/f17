package pl.app.character.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.character_template.application.domain.CharacterTemplate;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.CharacterRace;
import pl.app.common.shared.model.StatisticType;
import pl.app.common.shared.model.Statistics;

@Document(collection = "characters")
@Getter
public class Character {
    @Id
    private ObjectId id;
    @DocumentReference
    private CharacterTemplate template;

    private Statistics addedStatistics;

    private CharacterLevel level;

    @SuppressWarnings("unused")
    public Character() {
    }

    public Character(CharacterTemplate template) {
        this.id = ObjectId.get();
        this.template = template;
        this.addedStatistics = new Statistics();
        this.level = new CharacterLevel();
    }

    public Statistics getStatistics() {
        Statistics base = template.getBaseStatistics();
        Statistics perLevel = template.getPerLevelStatistics();
        return base.add(perLevel.multiply(Long.valueOf(this.level.getLevel()))).add(addedStatistics);
    }
    public void addStatistics(Long statisticQuantity, StatisticType statisticType){
        this.addedStatistics=this.addedStatistics.add(statisticQuantity, statisticType);
    }
    public Integer addExp(Long exp) {
        return level.addExp(exp);
    }
    public String getName(){
        return this.template.getName();
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
