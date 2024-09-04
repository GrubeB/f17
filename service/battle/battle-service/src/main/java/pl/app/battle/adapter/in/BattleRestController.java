package pl.app.battle.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.battle.application.port.in.BattleCommand;
import pl.app.battle.application.port.in.BattleService;
import pl.app.battle.query.BattleResultQueryService;
import pl.app.battle.query.TowerAttackResultQueryService;
import pl.app.battle.query.dto.BattleResultDto;
import pl.app.battle.query.dto.TowerAttackResultDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(BattleRestController.resourcePath)
@RequiredArgsConstructor
class BattleRestController {
    public static final String resourceName = "battles";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final BattleService battleService;
    private final BattleResultQueryService queryService;
    private final TowerAttackResultQueryService towerAttackResultQueryService;


    @PostMapping("/two-god-battle")
    public Mono<ResponseEntity<BattleResultDto>> startTwoGodBattle(
            @RequestBody BattleCommand.StartTwoGodBattleCommand command) {
        return battleService.startTwoGodBattle(command)
                .flatMap(domain -> queryService.fetchById(domain.getBattleId()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/attack-tower")
    public Mono<ResponseEntity<TowerAttackResultDto>> attackTower(
            @RequestBody BattleCommand.AttackTowerCommand command) {
        return battleService.attackTower(command)
                .flatMap(domain -> towerAttackResultQueryService.fetchById(domain.getTowerAttackId()))
                .map(ResponseEntity::ok);
    }
}
