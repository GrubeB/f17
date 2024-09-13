package pl.app.battle.adapter.out;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.app.battle.application.port.out.TowerRepository;
import pl.app.character.http.TowerLevelQueryControllerHttpInterface;
import pl.app.tower.dto.TowerLevelDto;
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
