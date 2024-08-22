package pl.app.item.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.account_equipment.dto.AccountEquipmentDto;
import pl.app.item.query.dto.OutfitDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "account-equipments",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface AccountEquipmentQueryControllerHttpInterface {
    @GetExchange
    Mono<ResponseEntity<Page<AccountEquipmentDto>>> fetchAllByPageable(Pageable pageable);

    @GetExchange("/{id}")
    Mono<ResponseEntity<AccountEquipmentDto>> fetchById(@PathVariable ObjectId id);
}
