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

    // return number of level ups
    Integer addExp(Long exp) {
        Integer numberOfLevelIncreased = 0;
        while (true) {
            Long expForNextLevel = getExpForLevel(level + numberOfLevelIncreased + 1);
            if (exp >= expForNextLevel) {
                numberOfLevelIncreased++;
            } else {
                break;
            }
        }
        this.exp = this.exp + exp;
        this.level = this.level + numberOfLevelIncreased;
        return numberOfLevelIncreased;
    }

    // TODO change to realistic numbers
    private Long getExpForLevel(Integer level) {
        return switch (level) {
            case 1 -> 0L;
            case 2 -> 100L;
            case 3 -> 200L;
            case 4 -> 300L;
            case 5 -> 500L;
            case 6 -> 700L;
            default -> getExpForLevel(level - 4) + getExpForLevel(level - 1);
        };
    }
}
