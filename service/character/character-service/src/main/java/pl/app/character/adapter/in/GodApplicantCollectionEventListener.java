package pl.app.character.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.application.port.in.CharacterService;
import pl.app.god_applicant_collection.application.domain.GodApplicantCollectionEvent;

@Component("pl.app.character.adapter.in.GodApplicantCollectionEventListener")
@RequiredArgsConstructor
class GodApplicantCollectionEventListener {
    private final Logger logger = LoggerFactory.getLogger(GodApplicantCollectionEventListener.class);
    private final CharacterService characterService;

    @KafkaListener(
            id = "god-applicant-rejected-event-listener--character",
            groupId = "${app.kafka.consumer.group-id}--character",
            topics = "${app.kafka.topic.god-applicant-rejected.name}"
    )
    public void godApplicantRejectedEvent(ConsumerRecord<ObjectId, GodApplicantCollectionEvent.GodApplicantRejectedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new CharacterCommand.RemoveCharacterCommand(
                event.getCharacterId()
        );
        characterService.removeCharacter(command).block();
    }

    @KafkaListener(
            id = "god-applicant-removed-event-listener--character",
            groupId = "${app.kafka.consumer.group-id}--character",
            topics = "${app.kafka.topic.god-applicant-removed.name}"
    )
    public void godApplicantRemovedEvent(ConsumerRecord<ObjectId, GodApplicantCollectionEvent.GodApplicantRemovedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new CharacterCommand.RemoveCharacterCommand(
                event.getCharacterId()
        );
        characterService.removeCharacter(command).block();
    }
}
