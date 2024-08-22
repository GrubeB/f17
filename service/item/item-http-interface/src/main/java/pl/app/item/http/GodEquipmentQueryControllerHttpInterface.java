package pl.app.item.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.god_equipment.dto.GodEquipmentDto;
import reactor.core.publisher.Mono;

@HttpExchange(
        url = "god-equipments",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface GodEquipmentQueryControllerHttpInterface {
    @GetExchange
    Mono<ResponseEntity<Page<GodEquipmentDto>>> fetchAllByPageable(Pageable pageable);

    @GetExchange("/{id}")
    Mono<ResponseEntity<GodEquipmentDto>> fetchById(@PathVariable ObjectId id);
}
