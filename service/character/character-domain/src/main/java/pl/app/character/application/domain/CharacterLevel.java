package pl.app.character.application.domain;

import lombok.Getter;

@Getter
public class CharacterLevel {
    private Integer level;
    private Long exp;

    public CharacterLevel() {
        this.level = 1;
        this.exp = 0L;
    }

    public Integer addExp(Long additionalExp) {
        this.exp += additionalExp;
        Integer newLevel = getLevelForExp(this.exp);
        Integer levelUps = newLevel - this.level;
        this.level = newLevel;
        return levelUps;
    }
    private Integer getLevelForExp(Long totalExp) {
        int calculatedLevel = 1;
        long expRequired = getExpForLevel(calculatedLevel + 1);
        while (totalExp >= expRequired) {
            calculatedLevel++;
            expRequired = getExpForLevel(calculatedLevel + 1);
        }
        return calculatedLevel;
    }

    private Long getExpForLevel(Integer targetLevel) {
        return switch (targetLevel) {
            case 1 -> 0L;
            case 2 -> 100L;
            case 3 -> 200L;
            case 4 -> 300L;
            case 5 -> 500L;
            case 6 -> 700L;
            default -> getExpForLevel(targetLevel - 4) + getExpForLevel(targetLevel - 1);
        };
    }

}
