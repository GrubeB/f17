package pl.app.character_status.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.character.application.domain.CharacterEvent;
import pl.app.character_status.application.domain.CharacterStatusType;
import pl.app.character_status.application.port.in.CharacterStatusCommand;
import pl.app.character_status.application.port.in.CharacterStatusService;

@Component("pl.app.character_status.adapter.in.CharacterEventListener")
@RequiredArgsConstructor
class CharacterEventListener {
    private final Logger logger = LoggerFactory.getLogger(CharacterEventListener.class);
    private final CharacterStatusService characterStatusService;

    @KafkaListener(
            id = "character-created-event-listener--character_status",
            groupId = "${app.kafka.consumer.group-id}--character_status",
            topics = "${app.kafka.topic.character-created.name}"
    )
    void changeStatus(ConsumerRecord<ObjectId, CharacterEvent.CharacterCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new CharacterStatusCommand.ChangeCharacterStatusCommand(
                event.getCharacterId(),
                CharacterStatusType.IDLE
        );
        characterStatusService.changeStatus(command).block();
    }
}
