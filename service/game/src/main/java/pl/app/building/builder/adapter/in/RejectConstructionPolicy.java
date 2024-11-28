package pl.app.building.builder.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.building.builder.application.port.in.BuilderCommand;
import pl.app.building.builder.application.port.in.BuilderService;
import pl.app.building.village_infrastructure.application.domain.VillageInfrastructureEvent;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component("pl.app.building.builder.adapter.in.RejectConstructionPolicy")
@RequiredArgsConstructor
class RejectConstructionPolicy {
    private final Logger logger = LoggerFactory.getLogger(RejectConstructionPolicy.class);
    private final BuilderService builderService;

    @KafkaListener(
            id = "village-infrastructure-building-level-down--event-listener--builder",
            groupId = "${app.kafka.consumer.group-id}--builder",
            topics = "${app.kafka.topic.village-infrastructure-building-level-down.name}"
    )
    public void constructAddedEvent(ConsumerRecord<ObjectId, VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        builderService.reject(new BuilderCommand.RejectBuildingConstructCommand(event.getVillageId(), event.getType())).subscribe();
    }
}
