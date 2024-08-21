package pl.app.battle.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@Document(collection = "battle_results")
public class BattleResult {
    private ObjectId battleId;
    private Boolean isTeam1Win;
    private List<ObjectId> team1CharacterIdsList;
    private List<ObjectId> team2CharacterIdsList;
    private List<CharacterResult> characterResults;
    @DocumentReference
    private BattleLog log;

    @SuppressWarnings("unused")
    public BattleResult() {
    }

    public BattleResult(ObjectId battleId, Set<BattleCharacter> team1, Set<BattleCharacter> team2) {
        this.battleId = battleId;
        this.team1CharacterIdsList = team1.stream().map(BattleCharacter::getId).collect(Collectors.toList());
        this.team2CharacterIdsList = team2.stream().map(BattleCharacter::getId).collect(Collectors.toList());
        this.characterResults = Stream.of(team1, team2)
                .flatMap(Set::stream)
                .map(character -> new CharacterResult(character.getId(), character.getType()))
                .collect(Collectors.toList());
    }

    public void setIsTeam1Win(Boolean result) {
        this.isTeam1Win = result;
    }

    public void setProgress(Map<ObjectId, Long> map) {
        map.forEach((id, exp) -> {
            getCharacterResultById(id).ifPresent(chr -> chr.setExp(exp));
        });
    }

    public void setMoney(Map<ObjectId, Long> map) {
        map.forEach((id, money) -> {
            getCharacterResultById(id).ifPresent(chr -> chr.setMoney(money));
        });
    }
    public void setLog(BattleLog log) {
        this.log = log;
    }
    private Optional<CharacterResult> getCharacterResultById(ObjectId objectId) {
        return characterResults.stream().filter(ch -> ch.getCharacterId().equals(objectId)).findAny();
    }

    @Getter
    public static class CharacterResult {
        private ObjectId characterId;
        private BattleCharacterType type;
        private Progress progress;
        private Loot loot;
        @SuppressWarnings("unused")
        public CharacterResult() {
        }
        public CharacterResult(ObjectId characterId, BattleCharacterType type) {
            this.characterId = characterId;
            this.type = type;
            this.progress = new Progress();
            this.loot = new Loot();
        }

        public void setExp(Long exp) {
            progress.setExp(exp);
        }

        public void setMoney(Long money) {
            loot.setMoney(money);
        }
    }

    @Getter
    public static class Progress {
        private Long exp;

        public Progress() {
            this.exp = 0L;
        }

        public void setExp(Long exp) {
            this.exp = exp;
        }
    }

    @Getter
    public static class Loot {
        private Long money;

        public Loot() {
            this.money = 0L;
        }

        public void setMoney(Long money) {
            this.money = money;
        }
    }
}
