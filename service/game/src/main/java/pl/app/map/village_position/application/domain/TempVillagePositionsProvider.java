package pl.app.map.village_position.application.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.app.map.map.application.domain.Position;
import pl.app.map.map.application.port.in.MapDomainRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class TempVillagePositionsProvider implements VillagePositionProvider {
    private final MapDomainRepository mapDomainRepository;

    @Override
    public Mono<Position> getNewVillagePosition() {
        return mapDomainRepository.fetch()
                .map(map -> {
                    Position nextPosition = map.getNextPosition()
                            .orElseThrow(VillagePositionException.NoMoreVillagePositionRemainedException::new);
                    return map.getPosition(nextPosition.getX(), nextPosition.getY());
                });
    }
}
