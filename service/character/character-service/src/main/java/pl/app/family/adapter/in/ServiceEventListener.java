package pl.app.family.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.god.application.domain.GodEvent;
import pl.app.family.application.port.in.FamilyCommand;
import pl.app.family.application.port.in.FamilyService;

@Component
@RequiredArgsConstructor
class ServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(ServiceEventListener.class);
    private final FamilyService familyService;

    @KafkaListener(
            id = "god-created-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.god-created.name}"
    )
    public void create(ConsumerRecord<ObjectId, GodEvent.GodCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new FamilyCommand.CreateGodFamilyCommand(
                event.getGodId()
        );
        familyService.create(command).block();
    }
}
