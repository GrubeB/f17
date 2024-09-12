package pl.app.battle.application.domain.battle;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Getter
@Document(collection = "battle_logs")
public class BattleLog {
    @Id
    private ObjectId objectId;
    private List<Object> events;

    public BattleLog() {
        this.objectId  = ObjectId.get();
        this.events = new LinkedList<>();
    }

    public void send(Object event) {
        this.events.add(event);
    }
}
