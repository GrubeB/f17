package pl.app.battle.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.character.application.domain.BattleCharacter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@Document(collection = "battle_results")
public class BattleResult {
    @Id
    private ObjectId battleId;
    private Boolean isTeam1Win;
    private List<ObjectId> team1CharacterIdsList;
    private List<ObjectId> team2CharacterIdsList;
    private List<CharacterResult> characterResults;
    @DocumentReference
    private BattleLog log;
    @Setter
    private Integer numberOfRounds;
    private Instant start;
    private Instant end;

    @SuppressWarnings("unused")
    public BattleResult() {
    }

    public BattleResult(ObjectId battleId, Set<BattleCharacter> team1, Set<BattleCharacter> team2) {
        this.battleId = battleId;
        this.team1CharacterIdsList = team1.stream().map(ch -> ch.getInfo().getId()).collect(Collectors.toList());
        this.team2CharacterIdsList = team2.stream().map(ch -> ch.getInfo().getId()).collect(Collectors.toList());
        this.characterResults = Stream.of(team1, team2)
                .flatMap(Set::stream)
                .map(character -> new CharacterResult(character.getInfo().getId(), character.getInfo().getGodId(), character.getInfo().getType()))
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

    public void setStart(Instant start) {
        this.start = start;
        this.end = start.plus(this.numberOfRounds, ChronoUnit.SECONDS);
    }


}
