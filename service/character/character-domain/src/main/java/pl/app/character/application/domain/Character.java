package pl.app.character.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.character_template.application.domain.CharacterTemplate;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.CharacterRace;

@Document(collection = "characters")
@Getter
public class Character {
    @Id
    private ObjectId id;
    @DocumentReference
    private CharacterTemplate template;
    private CharacterStatistics statistics;
    private CharacterLevel level;

    @SuppressWarnings("unused")
    public Character() {
    }

    public Character(CharacterTemplate template) {
        this.id = ObjectId.get();
        this.template = template;
        this.statistics = new CharacterStatistics(1L, 1L, 1L, 1000L, 15000L, 50000L, 15000L, 15000L);
        this.level = new CharacterLevel();
    }

    public Integer addExp(Long exp) {
        Integer numberOfLevelIncreased = level.addExp(exp);
        if (numberOfLevelIncreased == 0) {
            return 0;
        }
        for (int i = 0; i < numberOfLevelIncreased; i++) {
            statistics.addStatisticForLevel();
        }
        return numberOfLevelIncreased;
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
    public static Long getHp(Long persistence, CharacterProfession profession) {
        return switch (profession) {
            case MARKSMAN -> persistence * 40L;
            case WARRIOR -> persistence * 50L;
            case MAGE -> persistence * 35L;
            case SUPPORT -> persistence * 45L;
            case TANK -> persistence * 60L;
            default -> throw new IllegalStateException("Unexpected value: " + profession);
        };
    }

    public static Long getAttackPower(Long strength, CharacterProfession profession) {
        return (long) switch (profession) {
            case MARKSMAN -> strength * 3.5;
            case WARRIOR -> strength * 2.0;
            case MAGE -> strength * 4.0;
            case SUPPORT ->strength * 2.0;
            case TANK ->strength * 2.0;
            default -> throw new IllegalStateException("Unexpected value: " + profession);
        };
    }

    public static Long getDef(Long durability, CharacterProfession profession) {
        return switch (profession) {
            case MARKSMAN -> durability * 4L;
            case WARRIOR -> durability * 5L;
            case MAGE -> durability * 3L;
            case SUPPORT ->durability * 3L;
            case TANK ->durability * 5L;
            default -> throw new IllegalStateException("Unexpected value: " + profession);
        };
    }
}
