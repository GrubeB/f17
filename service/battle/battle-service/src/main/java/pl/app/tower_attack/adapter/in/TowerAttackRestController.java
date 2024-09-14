package pl.app.tower_attack.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.tower_attack.application.port.in.TowerAttackCommand;
import pl.app.tower_attack.application.port.in.TowerAttackService;
import pl.app.tower_attack.query.TowerAttackResultQueryService;
import pl.app.tower_attack.query.dto.TowerAttackResultDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(TowerAttackRestController.resourcePath)
@RequiredArgsConstructor
class TowerAttackRestController {
    public static final String resourceName = "tower-attacks";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final TowerAttackService towerAttackService;
    private final TowerAttackResultQueryService towerAttackResultQueryService;

    @PostMapping
    public Mono<ResponseEntity<TowerAttackResultDto>> attackTower(
            @RequestBody TowerAttackCommand.AttackTowerCommand command) {
        return towerAttackService.attackTower(command)
                .flatMap(domain -> towerAttackResultQueryService.fetchById(domain.getInfo().getTowerAttackId()))
                .map(ResponseEntity::ok);
    }
}
