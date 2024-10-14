package pl.app.map.village_position.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.app.common.mapper.BaseMapper;
import pl.app.map.village_position.application.domain.VillagePosition;
import pl.app.map.village_position.query.dto.VillagePositionDto;


@Component
@RequiredArgsConstructor
class VillagePositionMapper extends BaseMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    void init() {
        addMapper(VillagePosition.class, VillagePositionDto.class, e -> modelMapper.map(e, VillagePositionDto.class));
    }
}