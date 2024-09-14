package pl.app.tower_attack.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Getter
@Document(collection = "tower_attack_logs")
public class TowerAttackLog {
    @Id
    private ObjectId objectId;
    private List<Object> events;

    public TowerAttackLog() {
        this.objectId = ObjectId.get();
        this.events = new LinkedList<>();
    }

    public void send(Object event) {
        this.events.add(event);
    }
}
