package pl.app.comment.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.app.voting.application.domain.Voting;
import pl.app.voting.query.dto.VotingDto;
import pl.app.common.mapper.BaseMapper;

@Component
@RequiredArgsConstructor
class VotingMapper extends BaseMapper {
    private final ModelMapper modelMapper;
    @PostConstruct
    void init() {
        addMapper(Voting.class, VotingDto.class, e -> modelMapper.map(e, VotingDto.class));
    }
}
