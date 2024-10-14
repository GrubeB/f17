package pl.app.map.village_position.application.port.in;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.map.village_position.application.domain.VillagePosition;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
class VillagePositionDomainRepositoryImpl implements VillagePositionDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<VillagePosition> fetchAll() {
        return mongoTemplate.query(VillagePosition.class).all();
    }
}
