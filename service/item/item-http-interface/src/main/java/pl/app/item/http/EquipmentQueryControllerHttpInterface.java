package pl.app.item.http;

import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.common.shared.config.ResponsePage;
import pl.app.equipment.dto.EquipmentDto;
import reactor.core.publisher.Mono;

import java.util.List;

@HttpExchange(
        url = "god-equipments",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface EquipmentQueryControllerHttpInterface {
    @GetExchange("/{godId}")
    Mono<ResponseEntity<EquipmentDto>> fetchByGodId(
            @PathVariable ObjectId godId
    );

    @GetExchange
    Mono<ResponseEntity<ResponsePage<EquipmentDto>>> fetchAllByGodIds(
            @RequestParam("ids") List<ObjectId> godIds
    );

}
