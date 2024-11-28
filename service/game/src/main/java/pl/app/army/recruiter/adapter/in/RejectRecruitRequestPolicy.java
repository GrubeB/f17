package pl.app.army.recruiter.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.army.recruiter.application.port.in.RecruiterCommand;
import pl.app.army.recruiter.application.port.in.RecruiterService;
import pl.app.village.village.application.domain.VillageEvent;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class RejectRecruitRequestPolicy {
    private final Logger logger = LoggerFactory.getLogger(RejectRecruitRequestPolicy.class);
    private final RecruiterService service;

    @KafkaListener(
            id = "village-conquered--event-listener--recruiter",
            groupId = "${app.kafka.consumer.group-id}--recruiter",
            topics = "${app.kafka.topic.village-conquered.name}"
    )
    public void villageConqueredEvent(ConsumerRecord<ObjectId, VillageEvent.VillageConqueredEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        service.reject(new RecruiterCommand.RejectRecruitRequestCommand(event.getVillageId())).subscribe();
    }
}
