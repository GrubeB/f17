package pl.app.account_equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.account_equipment.dto.AccountEquipmentDto;
import pl.app.account_equipment.query.AccountEquipmentQueryService;
import pl.app.item.query.dto.OutfitDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AccountEquipmentQueryRestController.resourcePath)
@RequiredArgsConstructor
class AccountEquipmentQueryRestController {
    public static final String resourceName = "account-equipments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final AccountEquipmentQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<AccountEquipmentDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchByPageable(pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<AccountEquipmentDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
