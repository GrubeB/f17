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
import pl.app.recruitment.application.port.in.RecruitmentService;
import pl.app.recruitment.application.port.out.CharacterCreator;
import pl.app.recruitment.application.port.out.GodApplicantCreator;
import pl.app.recruitment.application.port.out.GodRepository;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class RecruitmentServiceImpl implements RecruitmentService {
    private static final Logger logger = LoggerFactory.getLogger(RecruitmentServiceImpl.class);

    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final GodRepository godRepository;
    private final CharacterCreator characterCreator;
    private final GodApplicantCreator godApplicantCreator;

    @Override
    public Mono<Void> post(RecruitmentCommand.PostRecruitmentAnnouncementCommand command) {
        logger.debug("posting recruitment announcement for god {}", command.getGodId());
        return godRepository.fetchById(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while posting recruitment announcement for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .zipWith(characterCreator.createRandomCharacter())
                .flatMap(t -> {
                    God t1 = t.getT1();
                    CharacterDto t2 = t.getT2();
                    var event = new RecruitmentEvent.RecruitmentAnnouncementPostedEvent(
                            t1.getId(), t2.getId()
                    );
                    return godApplicantCreator.create(t1.getId(), t2.getId())
                            .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getRecruitmentAnnouncementPosted().getName(), t1.getId(), event)).then())
                            .doOnSuccess(unused -> {
                                logger.debug("posted recruitment announcement for god: {}", t1.getId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).then();
                });
    }
}
