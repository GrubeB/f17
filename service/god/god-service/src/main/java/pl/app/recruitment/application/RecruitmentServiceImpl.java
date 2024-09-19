package pl.app.recruitment.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.character.query.dto.CharacterDto;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.god.application.domain.God;
import pl.app.recruitment.application.domain.RecruitmentEvent;
import pl.app.recruitment.application.port.in.RecruitmentCommand;
import pl.app.recruitment.application.port.in.RecruitmentResponse;
import pl.app.recruitment.application.port.in.RecruitmentService;
import pl.app.recruitment.application.port.out.CharacterCreator;
import pl.app.recruitment.application.port.out.GodApplicantCreator;
import pl.app.god.application.port.in.GodDomainRepository;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class RecruitmentServiceImpl implements RecruitmentService {
    private static final Logger logger = LoggerFactory.getLogger(RecruitmentServiceImpl.class);

    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final GodDomainRepository godDomainRepository;
    private final CharacterCreator characterCreator;
    private final GodApplicantCreator godApplicantCreator;

    @Override
    public Mono<RecruitmentResponse.RecruitmentAnnouncementPostedResponse> post(RecruitmentCommand.PostRecruitmentAnnouncementCommand command) {
        logger.debug("posting recruitment announcement for god {}", command.getGodId());
        return godDomainRepository.fetchById(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while posting recruitment announcement for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .zipWith(characterCreator.createRandomCharacter())
                .flatMap(t -> {
                    God god = t.getT1();
                    CharacterDto character = t.getT2();
                    var event = new RecruitmentEvent.RecruitmentAnnouncementPostedEvent(
                            god.getId(), character.getId()
                    );
                    return godApplicantCreator.create(god.getId(), character.getId())
                            .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getRecruitmentAnnouncementPosted().getName(), god.getId(), event)).then())
                            .doOnSuccess(unused -> {
                                logger.debug("posted recruitment announcement for god: {}", god.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).then(Mono.just(new RecruitmentResponse.RecruitmentAnnouncementPostedResponse(god.getId(), character.getId())));
                });
    }
}
