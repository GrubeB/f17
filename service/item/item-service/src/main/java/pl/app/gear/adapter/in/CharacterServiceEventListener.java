package pl.app.gear.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.character.application.domain.CharacterEvent;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.application.port.in.GearCommand;
import pl.app.gear.application.port.in.GearService;

@Component("pl.app.gear.adapter.in.CharacterServiceEventListener")
@RequiredArgsConstructor
class CharacterServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(CharacterServiceEventListener.class);
    private final GearService gearService;

    @KafkaListener(
            id = "character-created-event-listener--gear",
            groupId = "${app.kafka.consumer.group-id}--gear",
            topics = "${app.kafka.topic.character-created.name}"
    )
    public void characterCreatedEvent(ConsumerRecord<ObjectId, CharacterEvent.CharacterCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new GearCommand.CreateGearCommand(
                event.getCharacterId(),
                Gear.LootDomainObjectType.CHARACTER
        );
        gearService.crate(command).block();
    }

    @KafkaListener(
            id = "character-removed-event-listener--gear",
            groupId = "${app.kafka.consumer.group-id}--gear",
            topics = "${app.kafka.topic.character-removed.name}"
    )
    public void characterRemovedEvent(ConsumerRecord<ObjectId, CharacterEvent.CharacterRemovedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new GearCommand.RemoveGearCommand(
                event.getCharacterId(),
                Gear.LootDomainObjectType.CHARACTER
        );
        gearService.remove(command).block();
    }
}
