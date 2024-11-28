package pl.app.village.village.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.building.village_infrastructure.application.domain.VillageInfrastructure;
import pl.app.map.village_position.application.domain.VillagePosition;
import pl.app.player.player.model.Player;
import pl.app.resource.village_resource.application.domain.VillageResource;
import pl.app.army.village_army.application.domain.VillageArmy;
import pl.app.village.loyalty.application.domain.VillageLoyalty;
import pl.app.village.village.application.domain.Village;
import pl.app.village.village.application.domain.VillageType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "village")
public class VillageQuery {
    private ObjectId id;
    private VillageType type;
    private ObjectId ownerId;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'_id':?#{#self.ownerId} }")
    private Player player;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'_id':?#{#self._id} }")
    private VillageLoyalty villageLoyalty;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'_id':?#{#self._id} }")
    private VillagePosition villagePosition;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'_id':?#{#self._id} }")
    private VillageResource villageResource;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'_id':?#{#self._id} }")
    private VillageInfrastructure villageInfrastructure;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'_id':?#{#self._id} }")
    private VillageArmy villageArmy;
}
