package pl.app.item.http;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.app.common.shared.config.ResponsePage;
import pl.app.god_equipment.dto.CharacterGearDto;
import pl.app.monster_gear.dto.MonsterGearDto;
import reactor.core.publisher.Mono;

import java.util.List;

@HttpExchange(
        url = "monster-gears",
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE
)
public interface MonsterGearDtoQueryControllerHttpInterface {
    @GetExchange("/{monsterId}")
    Mono<ResponseEntity<MonsterGearDto>> fetchByMonsterId(
            @PathVariable ObjectId monsterId);

    @GetExchange
    Mono<ResponseEntity<ResponsePage<MonsterGearDto>>> fetchAllByMonsterIds(
            @RequestParam List<ObjectId> ids);
}
