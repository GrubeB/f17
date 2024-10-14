package pl.app.resource.village_resource.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.app.common.mapper.BaseMapper;
import pl.app.resource.village_resource.application.domain.VillageResource;
import pl.app.resource.village_resource.query.dto.VillageResourceDto;


@Component
@RequiredArgsConstructor
class VillageResourceMapper extends BaseMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    void init() {
        addMapper(VillageResource.class, VillageResourceDto.class, e -> modelMapper.map(e, VillageResourceDto.class));
    }
}