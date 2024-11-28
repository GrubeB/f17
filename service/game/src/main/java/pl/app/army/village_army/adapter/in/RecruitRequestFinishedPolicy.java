package pl.app.army.village_army.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.army.recruiter.application.domain.RecruiterEvent;
import pl.app.army.unit.model.Army;
import pl.app.army.village_army.application.port.in.VillageArmyCommand;
import pl.app.army.village_army.application.port.in.VillageArmyService;

import java.util.Map;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class RecruitRequestFinishedPolicy {
    private final Logger logger = LoggerFactory.getLogger(RecruitRequestFinishedPolicy.class);
    private final VillageArmyService service;

    @KafkaListener(
            id = "recruit-request-finished--event-listener--village_army",
            groupId = "${app.kafka.consumer.group-id}--village_army",
            topics = "${app.kafka.topic.recruit-request-finished.name}"
    )
    public void constructFinishedEvent(ConsumerRecord<ObjectId, RecruiterEvent.RecruitRequestFinishedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        service.add(new VillageArmyCommand.AddUnitsCommand(event.getVillageId(), Army.of(Map.of(event.getType(), event.getAmount()))))
                .subscribe();
    }

}
