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
import pl.app.god.application.domain.GodEvent;

@Component("pl.app.god_equipment.adapter.in.GodServiceEventListener")
@RequiredArgsConstructor
class GodServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(GodServiceEventListener.class);
    private final GodEquipmentService godEquipmentService;

    @KafkaListener(
            id = "god-created-event-listener--god-equipment",
            groupId = "${app.kafka.consumer.group-id}--god-equipment",
            topics = "${app.kafka.topic.god-created.name}"
    )
    void createGod(ConsumerRecord<ObjectId, GodEvent.GodCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new GodEquipmentCommand.CreateGodEquipmentCommand(
                event.getGodId()
        );
        godEquipmentService.createEquipment(command).block();
    }
}
