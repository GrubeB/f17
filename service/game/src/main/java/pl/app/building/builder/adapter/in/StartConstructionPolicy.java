package pl.app.building.builder.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.building.builder.application.domain.BuilderEvent;
import pl.app.building.builder.application.port.in.BuilderCommand;
import pl.app.building.builder.application.port.in.BuilderService;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component("pl.app.building.builder.adapter.in.StartConstructionPolicy")
@RequiredArgsConstructor
class StartConstructionPolicy {
    private final Logger logger = LoggerFactory.getLogger(StartConstructionPolicy.class);
    private final BuilderService builderService;

    @KafkaListener(
            id = "construct-added--event-listener--builder",
            groupId = "${app.kafka.consumer.group-id}--builder",
            topics = "${app.kafka.topic.construct-added.name}"
    )
    public void constructAddedEvent(ConsumerRecord<ObjectId, BuilderEvent.ConstructAddedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        startBuilding(event.getVillageId());
    }

    @KafkaListener(
            id = "construct-finished--event-listener--builder",
            groupId = "${app.kafka.consumer.group-id}--builder",
            topics = "${app.kafka.topic.construct-finished.name}"
    )
    public void constructFinishedEvent(ConsumerRecord<ObjectId, BuilderEvent.ConstructFinishedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        startBuilding(event.getVillageId());
    }

    @KafkaListener(
            id = "construct-rejected--event-listener--builder",
            groupId = "${app.kafka.consumer.group-id}--builder",
            topics = "${app.kafka.topic.construct-rejected.name}"
    )
    public void constructRejectedEvent(ConsumerRecord<ObjectId, BuilderEvent.ConstructRejectedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        startBuilding(event.getVillageId());
    }

    @KafkaListener(
            id = "construct-canceled--event-listener--builder",
            groupId = "${app.kafka.consumer.group-id}--builder",
            topics = "${app.kafka.topic.construct-canceled.name}"
    )
    public void constructCanceledEvent(ConsumerRecord<ObjectId, BuilderEvent.ConstructCanceledEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        startBuilding(event.getVillageId());
    }

    private void startBuilding(ObjectId villageId) {
        Mono.delay(Duration.of(1, ChronoUnit.SECONDS))
                .then(builderService.start(new BuilderCommand.StartBuildingToConstructCommand(villageId)))
                .subscribe();
    }
}
