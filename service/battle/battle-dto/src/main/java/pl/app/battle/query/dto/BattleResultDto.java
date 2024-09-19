package pl.app.battle.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import pl.app.battle.application.domain.BattleResult;
import pl.app.battle.application.domain.CharacterResult;
import pl.app.common.shared.model.Money;
import pl.app.unit.application.domain.BattleUnitType;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattleResultDto implements Serializable {
    private ObjectId battleId;
    private Boolean isTeam1Win;
    private List<TeamUnitDto> team1;
    private List<TeamUnitDto> team2;
    private List<CharacterResultDto> characterResults;
    private Instant start;
    private Instant end;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamUnitDto implements Serializable {
        private ObjectId id;
        private BattleUnitType type;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CharacterResultDto implements Serializable {
        private ObjectId characterId;
        private ObjectId godId;
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
            private Money money;
            private Set<LootItemDto> items;

            @Data
            @AllArgsConstructor
            @NoArgsConstructor
            public static class LootItemDto implements Serializable {
                private ObjectId itemTemplateId;
                private Integer amount;
            }
        }
    }
}
