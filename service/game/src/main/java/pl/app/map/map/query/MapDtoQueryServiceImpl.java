package pl.app.map.map.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.common.mapper.BaseMapper;
import pl.app.map.map.application.domain.Map;
import pl.app.map.map.application.domain.MapException;
import pl.app.map.map.application.domain.MapObject;
import pl.app.map.map.application.domain.Province;
import pl.app.map.map.query.dto.MapDto;
import pl.app.map.village_position.application.port.in.VillagePositionDomainRepository;
import pl.app.map.village_position.query.dto.VillagePositionDto;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class MapDtoQueryServiceImpl implements MapDtoQueryService {
    private final Mapper mapper;
    private final ReactiveMongoTemplate mongoTemplate;
    private final VillagePositionDomainRepository villagePositionDomainRepository;


    @Override
    public Mono<MapDto> fetch() {
        return mongoTemplate.query(Map.class).first()
                .flatMap(domain -> villagePositionDomainRepository.fetchAll().collect(Collectors.toSet())
                        .map(villagePositions -> {
                            domain.setVillagePositions(villagePositions);
                            return domain;
                        })
                )
                .switchIfEmpty(Mono.error(MapException.NotFoundMapException::new))
                .map(e -> mapper.map(e, MapDto.class));
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(Map.class, MapDto.class, this::mapToMapDto);
            addMapper(Province.class, MapDto.ProvinceDto.class, e -> modelMapper.map(e, MapDto.ProvinceDto.class));
            addMapper(MapObject.class, MapDto.MapObjectDto.class, e -> modelMapper.map(e, MapDto.MapObjectDto.class));
        }

        MapDto mapToMapDto(Map domain) {
            return MapDto.builder()
                    .positions(domain.getPositions())
                    .provinces(domain.getProvinces().stream().map(e -> map(e, MapDto.ProvinceDto.class)).collect(Collectors.toSet()))
                    .mapObjects(domain.getMapObjects().stream().map(e -> map(e, MapDto.MapObjectDto.class)).collect(Collectors.toSet()))
                    .villagePositions(domain.getVillagePositions().stream().map(e -> map(e, VillagePositionDto.class)).collect(Collectors.toSet()))
                    .build();
        }
    }
}
