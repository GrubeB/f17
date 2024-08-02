package pl.app.comment.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.app.comment.VotingQueryControllerHttpInterface;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.application.domain.VoteCounter;
import pl.app.comment.application.domain.Voting;
import pl.app.comment.query.dto.CommentContainerDto;
import pl.app.comment.query.dto.CommentDto;
import pl.app.common.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
class CommentContainerMapper extends BaseMapper {
    private final ModelMapper modelMapper;
    private final VotingQueryControllerHttpInterface votingQueryControllerHttpInterface;


    @PostConstruct
    void init() {
        addMapper(CommentContainer.class, CommentContainerDto.class, this::mapCommentContainer);
    }

    CommentContainerDto mapCommentContainer(CommentContainer model) {
        CommentContainerDto dto = modelMapper.map(model, CommentContainerDto.class);
        List<String> commentIds = dto.getAllComments().stream()
                .map(CommentDto::getId)
                .map(ObjectId::toString)
                .toList();
        if (!commentIds.isEmpty()) {
            List<Voting> votings = this.votingQueryControllerHttpInterface
                    .fetchByDomainObject("COMMENT", commentIds)
                    .map(ResponseEntity::getBody)
                    .defaultIfEmpty(new ArrayList<>())
                    .block();
            votings.forEach(v -> {
                final Long likes = v.getVotes().stream().filter(vc -> vc.getType().equals("LIKE")).findAny().map(VoteCounter::getNumber).orElse(0L);
                final Long dislikes = v.getVotes().stream().filter(vc -> vc.getType().equals("DISLIKE")).findAny().map(VoteCounter::getNumber).orElse(0L);
                dto.getCommentById(new ObjectId(v.getDomainObjectId()))
                        .ifPresent(c -> {
                            c.setNumberOfLikes(likes);
                            c.setNumberOfDislikes(dislikes);
                        });
            });
        }
        return dto;
    }
}
