package pl.app.god_equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.character.application.domain.CharacterEvent;
import pl.app.god_equipment.application.port.in.CharacterGearCommand;
import pl.app.god_equipment.application.port.in.CharacterGearService;

@Component
@RequiredArgsConstructor
class CharacterServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(CharacterServiceEventListener.class);
    private final CharacterGearService characterGearService;

    @KafkaListener(
            id = "character-created-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.character-created.name}"
    )
    public void createGod(ConsumerRecord<ObjectId, CharacterEvent.CharacterCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new CharacterGearCommand.CreateCharacterGearCommand(
                event.getCharacterId()
        );
        characterGearService.crateCharacterGear(command).block();
    }
}
