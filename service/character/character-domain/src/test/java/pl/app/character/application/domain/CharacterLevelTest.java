package pl.app.character.application.domain;

import org.junit.jupiter.api.Test;
import pl.app.character.application.domain.CharacterLevel;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class CharacterLevelTest {

    @Test
    void addExp_shouldBeFirstLevel_whenCreated() {
        CharacterLevel characterLevel = new CharacterLevel();
        assertThat(characterLevel.getLevel()).isEqualTo(1);
    }
    @Test
    void addExp_shouldAddExp_whenValidNumber() {
        CharacterLevel characterLevel = new CharacterLevel();
        characterLevel.addExp(100_300L);

        assertThat(characterLevel.getExp()).isEqualTo(100_300L);
    }
    @Test
    void addExp_shouldLevelUp_whenAddEnoughExp() {
        CharacterLevel characterLevel = new CharacterLevel();
        Integer numberOfLevelIncreased = characterLevel.addExp(100L);

        assertThat(characterLevel.getLevel()).isEqualTo(2);
        assertThat(numberOfLevelIncreased).isEqualTo(1);
    }
    @Test
    void addExp_shouldLevelUpThreeTimes_whenAddEnoughExp() {
        CharacterLevel characterLevel = new CharacterLevel();
        Integer numberOfLevelIncreased = characterLevel.addExp(300L);

        assertThat(characterLevel.getLevel()).isEqualTo(4);
        assertThat(numberOfLevelIncreased).isEqualTo(3);
    }
}