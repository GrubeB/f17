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
import pl.app.battle.query.dto.BattleResultDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(BattleRestController.resourcePath)
@RequiredArgsConstructor
class BattleRestController {
    public static final String resourceName = "battles";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final BattleService battleService;
    private final BattleResultQueryService queryService;


    @PostMapping("/two-god-battle")
    public Mono<ResponseEntity<BattleResultDto>> startTwoGodBattle(
            @RequestBody BattleCommand.StartTwoGodBattleCommand command) {
        return battleService.startTwoGodBattle(command)
                .flatMap(domain -> queryService.fetchById(domain.getBattleId()))
                .map(ResponseEntity::ok);
    }

}
