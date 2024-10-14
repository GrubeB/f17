package pl.app.map.village_position.application.domain;

import pl.app.map.map.application.domain.Position;

public interface VillagePositionProvider {
    Position getNewVillagePosition();
}
