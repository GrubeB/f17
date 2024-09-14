package pl.app.tower_attack.adapter.out;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.app.character.http.TowerLevelQueryControllerHttpInterface;
import pl.app.tower.dto.TowerLevelDto;
import pl.app.tower_attack.application.port.out.TowerRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class TowerRepositoryImpl implements TowerRepository {
    private final TowerLevelQueryControllerHttpInterface towerLevelQueryControllerHttpInterface;

    @Override
    public Mono<TowerLevelDto> getTowerLevel(Integer level) {
        return towerLevelQueryControllerHttpInterface.fetchByLevel(level).map(ResponseEntity::getBody);
    }
}
