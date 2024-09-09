package pl.app.loot.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.loot.aplication.domain.Loot;
import pl.app.loot.dto.LootDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LootDtoQueryService {
    Mono<LootDto> fetchByDomainObject(@NonNull ObjectId domainObjectId, @NonNull Loot.LootDomainObjectType domainObjectType);

    Mono<Page<LootDto>> fetchAllByPageable(Pageable pageable);

    Mono<Page<LootDto>> fetchAllByDomainObjectIds(List<ObjectId> domainObjectIds, @NonNull Loot.LootDomainObjectType domainObjectType, Pageable pageable);
}
