package pl.app.character.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "characters")
@Getter
public class Character {
    @Id
    private ObjectId id;
    private String name;
    private CharacterProfession profession;
    private CharacterStatistics statistics;
    private CharacterLevel level;

    @SuppressWarnings("unused")
    public Character() {
    }

    public Character(String name, CharacterProfession profession) {
        this.id = ObjectId.get();
        this.name = name;
        this.profession = profession;
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

    public static Long getHp(Long persistence, String profession) {
        return switch (profession) {
            case "MARKSMAN" -> persistence * 40L;
            case "WARRIOR" -> persistence * 50L;
            case "MAGE" -> persistence * 35L;
            default -> throw new IllegalStateException("Unexpected value: " + profession);
        };
    }

    public static Long getAttackPower(Long strength, String profession) {
        return (long) switch (profession) {
            case "MARKSMAN" -> strength * 3.5;
            case "WARRIOR" -> strength * 2.0;
            case "MAGE" -> strength * 4.0;
            default -> throw new IllegalStateException("Unexpected value: " + profession);
        };
    }

    public static Long getDef(Long durability, String profession) {
        return switch (profession) {
            case "MARKSMAN" -> durability * 4L;
            case "WARRIOR" -> durability * 5L;
            case "MAGE" -> durability * 3L;
            default -> throw new IllegalStateException("Unexpected value: " + profession);
        };
    }
}
