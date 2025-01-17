package pl.app.village.village.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.player.player.model.PlayerEvent;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class PlayerVillageCreationPolicy {
    private final Logger logger = LoggerFactory.getLogger(PlayerVillageCreationPolicy.class);
    private final VillageService villageService;

    @KafkaListener(
            id = "player-created-event-listener--player",
            groupId = "${app.kafka.consumer.group-id}--player",
            topics = "${app.kafka.topic.player-created.name}"
    )
    public void playerCreatedEvent(ConsumerRecord<ObjectId, PlayerEvent.PlayerCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(event.getPlayerId()))
                .subscribe();
    }
}
