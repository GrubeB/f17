package pl.app.energy.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.energy.application.port.in.EnergyCommand;
import pl.app.energy.application.port.in.EnergyService;
import pl.app.god.application.domain.GodEvent;

@Component(" pl.app.energy.adapter.in.GodServiceEventListener")
@RequiredArgsConstructor
class GodServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(GodServiceEventListener.class);
    private final EnergyService energyService;

    @KafkaListener(
            id = "god-created-event-listener--gear",
            groupId = "${app.kafka.consumer.group-id}--gear",
            topics = "${app.kafka.topic.god-created.name}"
    )
    public void godCreatedEvent(ConsumerRecord<ObjectId, GodEvent.GodCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new EnergyCommand.CreateEnergyCommand(
                event.getGodId()
        );
        energyService.create(command).block();
    }
}
