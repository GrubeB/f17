package pl.app.village.village_effect.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.village.village.application.domain.VillageEvent;
import pl.app.village.village_effect.application.port.in.VillageEffectCommand;
import pl.app.village.village_effect.application.port.in.VillageEffectService;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class RejectVillageEffectsPolicy {
    private final Logger logger = LoggerFactory.getLogger(RejectVillageEffectsPolicy.class);
    private final VillageEffectService service;

    @KafkaListener(
            id = "village-conquered--event-listener--village_effect",
            groupId = "${app.kafka.consumer.group-id}--village_effect",
            topics = "${app.kafka.topic.village-conquered.name}"
    )
    public void villageConqueredEvent(ConsumerRecord<ObjectId, VillageEvent.VillageConqueredEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        service.reject(new VillageEffectCommand.RejectAllEffectsCommand(event.getVillageId())).subscribe();
    }
}
