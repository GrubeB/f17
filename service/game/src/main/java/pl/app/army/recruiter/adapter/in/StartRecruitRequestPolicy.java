package pl.app.army.recruiter.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.army.recruiter.application.domain.RecruiterEvent;
import pl.app.army.recruiter.application.port.in.RecruiterCommand;
import pl.app.army.recruiter.application.port.in.RecruiterService;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class StartRecruitRequestPolicy {
    private final Logger logger = LoggerFactory.getLogger(StartRecruitRequestPolicy.class);
    private final RecruiterService service;

    @KafkaListener(
            id = "recruit-request-added--event-listener--recruiter",
            groupId = "${app.kafka.consumer.group-id}--recruiter",
            topics = "${app.kafka.topic.recruit-request-added.name}"
    )
    public void recruitRequestAddedEvent(ConsumerRecord<ObjectId, RecruiterEvent.RecruitRequestAddedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        startRequest(event.getVillageId());
    }

    @KafkaListener(
            id = "recruit-request-finished--event-listener--recruiter",
            groupId = "${app.kafka.consumer.group-id}--recruiter",
            topics = "${app.kafka.topic.recruit-request-finished.name}"
    )
    public void recruitRequestFinishedEvent(ConsumerRecord<ObjectId, RecruiterEvent.RecruitRequestFinishedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        startRequest(event.getVillageId());
    }

    @KafkaListener(
            id = "recruit-request-canceled--event-listener--recruiter",
            groupId = "${app.kafka.consumer.group-id}--recruiter",
            topics = "${app.kafka.topic.recruit-request-canceled.name}"
    )
    public void recruitRequestCanceledEvent(ConsumerRecord<ObjectId, RecruiterEvent.RecruitRequestCanceledEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        startRequest(event.getVillageId());
    }

    private void startRequest(ObjectId villageId) {
        Mono.delay(Duration.of(1, ChronoUnit.SECONDS))
                .then(service.start(new RecruiterCommand.StartRecruitRequestCommand(villageId)))
                .subscribe();
    }
}
