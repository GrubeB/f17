package pl.app.account_equipment.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.account_equipment.dto.AccountEquipmentDto;
import pl.app.item.query.dto.OutfitDto;
import reactor.core.publisher.Mono;

public interface AccountEquipmentQueryService {
    Mono<AccountEquipmentDto> fetchById(@NonNull ObjectId id);

    Mono<Page<AccountEquipmentDto>> fetchByPageable(Pageable pageable);
}
