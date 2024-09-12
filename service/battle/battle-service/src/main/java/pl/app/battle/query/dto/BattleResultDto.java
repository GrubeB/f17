package pl.app.battle.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.battle.application.domain.battle.BattleCharacterType;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattleResultDto implements Serializable {
    private ObjectId battleId;
    private Boolean isTeam1Win;
    private List<ObjectId> team1CharacterIdsList;
    private List<ObjectId> team2CharacterIdsList;
    private List<CharacterResultDto> characterResults;
    private Instant start;
    private Instant end;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CharacterResultDto implements Serializable {
        private ObjectId characterId;
        private ObjectId godId;
        private BattleCharacterType type;
        private ProgressDto progress;
        private LootDto loot;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ProgressDto implements Serializable {
            private Long exp;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class LootDto implements Serializable {
            private Long money;
        }
    }
}
