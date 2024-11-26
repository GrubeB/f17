package pl.app.player.player.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.app.common.mapper.BaseMapper;
import pl.app.player.player.model.Player;
import pl.app.player.player.service.dto.PlayerDto;

@Component
@RequiredArgsConstructor
class PlayerMapper extends BaseMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    void init() {
        addMapper(Player.class, PlayerDto.class, e -> modelMapper.map(e, PlayerDto.class));
    }
}