package pl.app.map.village_position.application.domain;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.app.map.map.application.domain.Map;
import pl.app.map.map.application.domain.Position;
import pl.app.map.map.application.port.in.MapDomainRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
class TempVillagePositionsProvider implements VillagePositionProvider {
    private final MapDomainRepository mapDomainRepository;
    private Map map;

    @PostConstruct
    void init() {
        mapDomainRepository.fetch().doOnSuccess(m -> this.map = m);
    }

    @Override
    public Position getNewVillagePosition() {
        Set<Position> noOccupiedPositions = map.getNoOccupiedPositions();
        if (noOccupiedPositions.isEmpty()) {
            throw new VillagePositionException.NoMoreVillagePositionRemainedException();
        }
        Position next = noOccupiedPositions.iterator().next();
        return map.getPosition(next.getX(), next.getY());
    }
}
