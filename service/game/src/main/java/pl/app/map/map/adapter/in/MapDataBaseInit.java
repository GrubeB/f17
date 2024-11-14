package pl.app.map.map.adapter.in;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.map.map.application.domain.Map;
import pl.app.map.map.application.domain.Position;
import pl.app.map.map.application.domain.Province;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
class MapDataBaseInit {
    private static final Logger logger = LoggerFactory.getLogger(MapDataBaseInit.class);
    private final ReactiveMongoTemplate mongoTemplate;

    @PostConstruct
    void initMap() {
        Map newMap = getBaseMap();
        mongoTemplate.query(Map.class).one()
                .switchIfEmpty(Mono.fromCallable(() ->
                                mongoTemplate.save(newMap)
                        ).doOnSubscribe(subscription ->
                                logger.debug("inserting default map")
                        ).flatMap(Function.identity()).doOnSuccess(domain ->
                                logger.debug("Inserted default map")
                        ).doOnError(e ->
                                logger.error("exception occurred while inserting default map, exception: {}", e.toString())
                        )
                )
                .subscribe();
    }

    private Map getBaseMap() {
        var province1 = new Province("Kotowo");
        var province2 = new Province("Kotowo2");
        var positions = getPositions(province1, province2);
        return new Map(11, 11, Set.of(province1, province2), positions);
    }

    private HashSet<Position> getPositions(Province province1, Province province2) {
        return Stream.of(
                        new Position(0, 0, province1),
                        new Position(0, 1, province1),
                        new Position(0, 2, province1),
                        new Position(0, 3, province1),
                        new Position(0, 4, province1),
                        new Position(0, 5, province1),
                        new Position(0, 6, province1),
                        new Position(0, 7, province1),
                        new Position(0, 8, province1),
                        new Position(0, 9, province1),
                        new Position(0, 10, province1),

                        new Position(1, 0, province1),
                        new Position(1, 1, province1),
                        new Position(1, 2, province1),
                        new Position(1, 3, province1),
                        new Position(1, 4, province1),
                        new Position(1, 5, province1),
                        new Position(1, 6, province1),
                        new Position(1, 7, province1),
                        new Position(1, 8, province1),
                        new Position(1, 9, province1),
                        new Position(1, 10, province1),

                        new Position(2, 0, province1),
                        new Position(2, 1, province1),
                        new Position(2, 2, province1),
                        new Position(2, 3, province1),
                        new Position(2, 4, province1),
                        new Position(2, 5, province1),
                        new Position(2, 6, province1),
                        new Position(2, 7, province1),
                        new Position(2, 8, province1),
                        new Position(2, 9, province1),
                        new Position(2, 10, province1),

                        new Position(3, 0, province1),
                        new Position(3, 1, province1),
                        new Position(3, 2, province1),
                        new Position(3, 3, province1),
                        new Position(3, 4, province1),
                        new Position(3, 5, province1),
                        new Position(3, 6, province1),
                        new Position(3, 7, province1),
                        new Position(3, 8, province1),
                        new Position(3, 9, province1),
                        new Position(3, 10, province1),

                        new Position(4, 0, province1),
                        new Position(4, 1, province1),
                        new Position(4, 2, province1),
                        new Position(4, 3, province1),
                        new Position(4, 4, province1),
                        new Position(4, 5, province1),
                        new Position(4, 6, province1),
                        new Position(4, 7, province1),
                        new Position(4, 8, province1),
                        new Position(4, 9, province1),
                        new Position(4, 10, province1),

                        new Position(5, 0, province1),
                        new Position(5, 1, province1),
                        new Position(5, 2, province1),
                        new Position(5, 3, province1),
                        new Position(5, 4, province1),
                        new Position(5, 5, province1),
                        new Position(5, 6, province1),
                        new Position(5, 7, province1),
                        new Position(5, 8, province1),
                        new Position(5, 9, province1),
                        new Position(5, 10, province1),

                        new Position(6, 0, province2),
                        new Position(6, 1, province2),
                        new Position(6, 2, province2),
                        new Position(6, 3, province2),
                        new Position(6, 4, province2),
                        new Position(6, 5, province2),
                        new Position(6, 6, province2),
                        new Position(6, 7, province2),
                        new Position(6, 8, province2),
                        new Position(6, 9, province2),
                        new Position(6, 10, province2),

                        new Position(7, 0, province2),
                        new Position(7, 1, province2),
                        new Position(7, 2, province2),
                        new Position(7, 3, province2),
                        new Position(7, 4, province2),
                        new Position(7, 5, province2),
                        new Position(7, 6, province2),
                        new Position(7, 7, province2),
                        new Position(7, 8, province2),
                        new Position(7, 9, province2),
                        new Position(7, 10, province2),

                        new Position(7, 0, province2),
                        new Position(7, 1, province2),
                        new Position(7, 2, province2),
                        new Position(7, 3, province2),
                        new Position(7, 4, province2),
                        new Position(7, 5, province2),
                        new Position(7, 6, province2),
                        new Position(7, 7, province2),
                        new Position(7, 8, province2),
                        new Position(7, 9, province2),
                        new Position(7, 10, province2),

                        new Position(8, 0, province2),
                        new Position(8, 1, province2),
                        new Position(8, 2, province2),
                        new Position(8, 3, province2),
                        new Position(8, 4, province2),
                        new Position(8, 5, province2),
                        new Position(8, 6, province2),
                        new Position(8, 7, province2),
                        new Position(8, 8, province2),
                        new Position(8, 9, province2),
                        new Position(8, 10, province2),

                        new Position(9, 0, province2),
                        new Position(9, 1, province2),
                        new Position(9, 2, province2),
                        new Position(9, 3, province2),
                        new Position(9, 4, province2),
                        new Position(9, 5, province2),
                        new Position(9, 6, province2),
                        new Position(9, 7, province2),
                        new Position(9, 8, province2),
                        new Position(9, 9, province2),
                        new Position(9, 10, province2),

                        new Position(10, 0, province2),
                        new Position(10, 1, province2),
                        new Position(10, 2, province2),
                        new Position(10, 3, province2),
                        new Position(10, 4, province2),
                        new Position(10, 5, province2),
                        new Position(10, 6, province2),
                        new Position(10, 7, province2),
                        new Position(10, 8, province2),
                        new Position(10, 9, province2),
                        new Position(10, 10, province2)
                )
                .collect(Collectors.toCollection(HashSet::new));
    }
}
