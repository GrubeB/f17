package pl.app.tower.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.monster.application.domain.Monster;

import java.util.Objects;
import java.util.Set;

@Document(collection = "tower_level")
@Getter
public class TowerLevel {
    @Id
    private ObjectId id;
    private Integer level;
    @Setter
    @DocumentReference
    private Set<Monster> monsters;
    private Integer minNumberOfMonstersInBattle;
    private Integer maxNumberOfMonstersInBattle;
    @Setter
    private Integer energyCost;

    public TowerLevel(Integer level, Set<Monster> monsters) {
        this.id = ObjectId.get();
        this.level = level;
        this.monsters = monsters;
        this.minNumberOfMonstersInBattle = 1;
        this.maxNumberOfMonstersInBattle = 5;
    }

    public void setNumberOfMonsters(Integer min, Integer max) {
        if (Objects.nonNull(min) && min > 0) {
            this.minNumberOfMonstersInBattle = min;
        }
        if (Objects.nonNull(max) && max > 0) {
            this.maxNumberOfMonstersInBattle = max;
        }
    }
}
