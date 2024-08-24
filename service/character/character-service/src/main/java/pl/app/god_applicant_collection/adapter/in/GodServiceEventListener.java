package pl.app.god_applicant_collection.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.god.application.domain.GodEvent;
import pl.app.god_applicant_collection.application.port.in.GodApplicantCollectionCommand;
import pl.app.god_applicant_collection.application.port.in.GodApplicantCollectionService;

@Component("pl.app.god_applicant_collection.adapter.in.GodServiceEventListener")
@RequiredArgsConstructor
class GodServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(GodServiceEventListener.class);
    private final GodApplicantCollectionService godApplicantCollectionService;

    @KafkaListener(
            id = "god-created-event-listener--god-application-collection",
            groupId = "${app.kafka.consumer.group-id}--god-application-collection",
            topics = "${app.kafka.topic.god-created.name}"
    )
    public void create(ConsumerRecord<ObjectId, GodEvent.GodCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new GodApplicantCollectionCommand.CreateGofApplicantCollectionCommand(
                event.getGodId()
        );
        godApplicantCollectionService.createGodApplicantCollection(command).block();
    }
}
