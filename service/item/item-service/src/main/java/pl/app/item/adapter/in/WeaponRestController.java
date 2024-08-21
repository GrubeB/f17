package pl.app.item.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.item.application.port.in.ItemCommand;
import pl.app.item.application.port.in.ItemService;
import pl.app.item.query.WeaponQueryService;
import pl.app.item.query.dto.WeaponDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(WeaponRestController.resourcePath)
@RequiredArgsConstructor
class WeaponRestController {
    public static final String resourceName = "weapons";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final ItemService service;
    private final WeaponQueryService queryService;

    @PostMapping
    public Mono<ResponseEntity<WeaponDto>> createWeapon(@RequestBody ItemCommand.CreateWeaponCommand command) {
        return service.createWeapon(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
