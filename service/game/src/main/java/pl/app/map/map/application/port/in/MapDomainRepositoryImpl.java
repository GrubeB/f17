package pl.app.map.map.application.port.in;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.map.map.application.domain.Map;
import pl.app.map.map.application.domain.MapException;
import pl.app.map.village_position.application.port.in.VillagePositionDomainRepository;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class MapDomainRepositoryImpl implements MapDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;
    private final VillagePositionDomainRepository villagePositionDomainRepository;

    @Override
    public Mono<Map> fetch() {
        return mongoTemplate.query(Map.class).first()
                .flatMap(domain -> villagePositionDomainRepository.fetchAll().collect(Collectors.toSet())
                        .map(villagePositions -> {
                            domain.setVillagePositions(villagePositions);
                            return domain;
                        })
                )
                .switchIfEmpty(Mono.error(MapException.NotFoundMapException::new));
    }
}
