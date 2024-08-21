package pl.app.character.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.app.character.application.domain.Character;
import pl.app.character.query.dto.CharacterDto;
import pl.app.common.mapper.BaseMapper;
@Component
@RequiredArgsConstructor
class CharacterMapper extends BaseMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    void init() {
        addMapper(Character.class, CharacterDto.class, e -> modelMapper.map(e, CharacterDto.class));
    }
}
