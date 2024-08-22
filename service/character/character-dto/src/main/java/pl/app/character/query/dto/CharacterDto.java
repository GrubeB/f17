package pl.app.character.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterDto implements Serializable {
    private ObjectId id;
    private String name;
    private String profession;
    private LevelDto level;
    private StatisticsDto statistics;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatisticsDto implements Serializable {
        private Long persistence;
        private Long durability;
        private Long strength;
        private Long speed;
        private Long criticalRate;
        private Long criticalDamage;
        private Long accuracy;
        private Long resistance;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LevelDto implements Serializable {
        private Integer level;
        private Long exp;
    }
}
