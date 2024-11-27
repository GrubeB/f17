package pl.app.army.village_army.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.army.village_army.application.port.in.VillageArmyCommand;
import pl.app.army.village_army.application.port.in.VillageArmyService;
import pl.app.village.village.application.domain.VillageEvent;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class RemoveAllUnitsPolicy {
    private final Logger logger = LoggerFactory.getLogger(RemoveAllUnitsPolicy.class);
    private final VillageArmyService service;

    @KafkaListener(
            id = "village-conquered--event-listener--village_army",
            groupId = "${app.kafka.consumer.group-id}--village_army",
            topics = "${app.kafka.topic.village-conquered.name}"
    )
    public void villageConqueredEvent(ConsumerRecord<ObjectId, VillageEvent.VillageConqueredEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        service.removeAllUnitsFromVillage(new VillageArmyCommand.RemoveAllUnitsFromVillageCommand(event.getVillageId()))
                .subscribe();
    }
}
