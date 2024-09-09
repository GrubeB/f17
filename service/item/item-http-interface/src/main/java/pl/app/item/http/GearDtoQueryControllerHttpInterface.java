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
import pl.app.gear.aplication.domain.GearException;
import pl.app.gear.dto.GearDto;
import pl.app.item.query.dto.OutfitDto;
import reactor.core.publisher.Mono;

import java.util.List;

@HttpExchange(
        url = "/domainObjectTypes/{domainObjectType}/gears",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface GearDtoQueryControllerHttpInterface {
    @GetExchange("/{domainObjectId}")
    Mono<ResponseEntity<GearDto>> fetchByDomainObject(
            @PathVariable ObjectId domainObjectId,
            @PathVariable Gear.LootDomainObjectType domainObjectType
    );

    @GetExchange
    Mono<ResponseEntity<ResponsePage<GearDto>>> fetchByDomainObject(
            @PathVariable Gear.LootDomainObjectType domainObjectType,
            @RequestParam(required = false) List<ObjectId> ids
    );

}
