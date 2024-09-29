package pl.app.god.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.common.mapper.BaseMapper;
import pl.app.energy.query.EnergyQueryService;
import pl.app.energy.query.dto.EnergyDto;
import pl.app.god.query.dto.GodAggregateDto;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Service
@RequiredArgsConstructor
class GodAggregateQueryServiceImpl implements GodAggregateQueryService {
    private final Mapper mapper;
    private final GodQueryService godQueryService;
    private final EnergyQueryService energyQueryService;

    @Override
    public Mono<GodAggregateDto> fetchById(@NonNull ObjectId id) {
        return Mono.zip(
                        godQueryService.fetchById(id),
                        energyQueryService.fetchByGodId(id)
                )
                .map(t -> mapper.map(t.getT1(), t.getT2(), GodAggregateDto.class));
    }

    @Override
    public Mono<Page<GodAggregateDto>> fetchAllByPageable(Pageable pageable) {
        return godQueryService.fetchAllByPageable(pageable)
                .flatMap(gods -> {
                    var godIds = gods.stream().map(GodDto::getId).toList();
                    return energyQueryService.fetchAllByGodIds(godIds).map(energyDtos -> Tuples.of(gods, energyDtos));
                })
                .map(t -> Tuples.of(
                        t.getT1(),
                        t.getT1().stream().map(god -> {
                            var energyDto = t.getT2().stream().filter(e -> e.getGodId().equals(god.getId())).findAny().orElse(new EnergyDto());
                            return mapper.map(god, energyDto, GodAggregateDto.class);
                        }).toList()
                ))
                .map(t -> new PageImpl<>(t.getT2(), pageable, t.getT1().getTotalElements()));
    }


    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(GodDto.class, EnergyDto.class, GodAggregateDto.class, this::mapToGodAggregateDto);
        }

        GodAggregateDto mapToGodAggregateDto(GodDto god, EnergyDto energy) {
            return new GodAggregateDto(god.getId(), god, energy);
        }
    }
}
