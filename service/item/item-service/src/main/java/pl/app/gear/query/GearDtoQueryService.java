package pl.app.gear.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.dto.GearDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GearDtoQueryService {
    Mono<GearDto> fetchByDomainObject(@NonNull ObjectId domainObjectId, @NonNull Gear.LootDomainObjectType domainObjectType);

    Mono<Page<GearDto>> fetchAllByPageable(Pageable pageable);

    Mono<Page<GearDto>> fetchAllByDomainObjectIds(List<ObjectId> domainObjectIds, @NonNull Gear.LootDomainObjectType domainObjectType, Pageable pageable);
}
