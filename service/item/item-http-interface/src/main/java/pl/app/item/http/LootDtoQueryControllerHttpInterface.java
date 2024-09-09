package pl.app.item.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.common.shared.config.ResponsePage;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.dto.GearDto;
import pl.app.loot.aplication.domain.Loot;
import pl.app.loot.aplication.domain.LootException;
import pl.app.loot.dto.LootDto;
import reactor.core.publisher.Mono;

import java.util.List;

@HttpExchange(
        url = "/domainObjectTypes/{domainObjectType}/loots",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface LootDtoQueryControllerHttpInterface {
    @GetExchange("/{domainObjectId}")
    Mono<ResponseEntity<LootDto>> fetchByDomainObject(
            @PathVariable ObjectId domainObjectId,
            @PathVariable Loot.LootDomainObjectType domainObjectType
    );

    @GetExchange
    Mono<ResponseEntity<ResponsePage<LootDto>>> fetchByDomainObject(
            @PathVariable Loot.LootDomainObjectType domainObjectType,
            @RequestParam(required = false) List<ObjectId> ids
    );
}
