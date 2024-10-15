package pl.app.map.village_position.application.domain;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.app.map.map.application.domain.Position;
import pl.app.map.map.application.port.in.MapDomainRepository;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
class TempVillagePositionsProvider implements VillagePositionProvider {
    private final MapDomainRepository mapDomainRepository;

    @PostConstruct
    void init() {
    }

    @Override
    public Mono<Position> getNewVillagePosition() {
        return mapDomainRepository.fetch()
                .map(map -> {
                    Set<Position> noOccupiedPositions = map.getNoOccupiedPositions();
                    if (noOccupiedPositions.isEmpty()) {
                        throw new VillagePositionException.NoMoreVillagePositionRemainedException();
                    }
                    Position next = noOccupiedPositions.iterator().next();
                    return map.getPosition(next.getX(), next.getY());
                });
    }
}
