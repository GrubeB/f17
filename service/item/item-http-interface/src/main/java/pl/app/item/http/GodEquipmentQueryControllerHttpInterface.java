package pl.app.item.http;

import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.common.shared.config.ResponsePage;
import pl.app.god_equipment.dto.GodEquipmentDto;
import reactor.core.publisher.Mono;

import java.util.List;

@HttpExchange(
        url = "god-equipments",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface GodEquipmentQueryControllerHttpInterface {
    @GetExchange("/{godId}")
    Mono<ResponseEntity<GodEquipmentDto>> fetchByGodId(@PathVariable ObjectId godId);

    @GetExchange
    Mono<ResponseEntity<ResponsePage<GodEquipmentDto>>> fetchAllByGodIds(@RequestParam List<ObjectId> ids);

}
