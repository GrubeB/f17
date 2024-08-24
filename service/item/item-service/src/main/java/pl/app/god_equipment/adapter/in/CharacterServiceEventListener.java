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
import pl.app.god_equipment.application.port.in.GodEquipmentCommand;
import pl.app.god_equipment.application.port.in.GodEquipmentService;
import pl.app.god_family.application.domain.GodFamilyEvent;

@Component("pl.app.god_equipment.adapter.in.CharacterServiceEventListener")
@RequiredArgsConstructor
class CharacterServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(CharacterServiceEventListener.class);
    private final CharacterGearService characterGearService;
    private final GodEquipmentService godEquipmentService;

    @KafkaListener(
            id = "character-created-event-listener--god-equipment",
            groupId = "${app.kafka.consumer.group-id}--god-equipment",
            topics = "${app.kafka.topic.character-created.name}"
    )
    public void characterCreatedEvent(ConsumerRecord<ObjectId, CharacterEvent.CharacterCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new CharacterGearCommand.CreateCharacterGearCommand(
                event.getCharacterId()
        );
        characterGearService.crateCharacterGear(command).block();
    }

    @KafkaListener(
            id = "character-removed-event-listener--god-equipment",
            groupId = "${app.kafka.consumer.group-id}--god-equipment",
            topics = "${app.kafka.topic.character-removed.name}"
    )
    public void characterRemovedEvent(ConsumerRecord<ObjectId, CharacterEvent.CharacterRemovedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new CharacterGearCommand.RemoveCharacterGearCommand(
                event.getCharacterId()
        );
        characterGearService.removeCharacterGear(command).block();
    }

    @KafkaListener(
            id = "character-added-to-god-family-event-listener--god-equipment",
            groupId = "${app.kafka.consumer.group-id}--god-equipment",
            topics = "${app.kafka.topic.character-added-to-god-family.name}"
    )
    public void characterAddedToFamilyEvent(ConsumerRecord<ObjectId, GodFamilyEvent.CharacterAddedToFamilyEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new GodEquipmentCommand.AddCharacterGearToGodEquipmentCommand(
                event.getGodId(), event.getCharacterId()
        );
        godEquipmentService.addCharacterGearToGodEquipment(command).block();
    }

    @KafkaListener(
            id = "character-removed-from-god-family-event-listener--god-equipment",
            groupId = "${app.kafka.consumer.group-id}--god-equipment",
            topics = "${app.kafka.topic.character-removed-from-god-family.name}"
    )
    public void characterRemovedFromFamilyEvent(ConsumerRecord<ObjectId, GodFamilyEvent.CharacterRemovedFromFamilyEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new GodEquipmentCommand.RemoveCharacterGearFromGodEquipmentCommand(
                event.getGodId(), event.getCharacterId()
        );
        godEquipmentService.removeCharacterGearFromGodEquipment(command).block();
    }
}
