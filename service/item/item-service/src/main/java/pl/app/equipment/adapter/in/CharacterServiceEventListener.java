package pl.app.equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.equipment.application.port.in.GodEquipmentCommand;
import pl.app.equipment.application.port.in.GodEquipmentService;
import pl.app.family.application.domain.FamilyEvent;

@Component("pl.app.equipment.adapter.in.CharacterServiceEventListener")
@RequiredArgsConstructor
class CharacterServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(CharacterServiceEventListener.class);
    private final GodEquipmentService godEquipmentService;

    @KafkaListener(
            id = "character-added-to-family-event-listener--equipment",
            groupId = "${app.kafka.consumer.group-id}--equipment",
            topics = "${app.kafka.topic.character-added-to-family.name}"
    )
    public void characterAddedToFamilyEvent(ConsumerRecord<ObjectId, FamilyEvent.CharacterAddedToFamilyEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new GodEquipmentCommand.AddCharacterGearToGodEquipmentCommand(
                event.getGodId(), event.getCharacterId()
        );
        godEquipmentService.addCharacterGearToGodEquipment(command).block();
    }

    @KafkaListener(
            id = "character-removed-from-family-event-listener--equipment",
            groupId = "${app.kafka.consumer.group-id}--equipment",
            topics = "${app.kafka.topic.character-removed-from-family.name}"
    )
    public void characterRemovedFromFamilyEvent(ConsumerRecord<ObjectId, FamilyEvent.CharacterRemovedFromFamilyEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new GodEquipmentCommand.RemoveCharacterGearFromGodEquipmentCommand(
                event.getGodId(), event.getCharacterId()
        );
        godEquipmentService.removeCharacterGearFromGodEquipment(command).block();
    }
}
