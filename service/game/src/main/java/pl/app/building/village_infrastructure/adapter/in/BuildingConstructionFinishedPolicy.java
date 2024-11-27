package pl.app.building.village_infrastructure.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.building.builder.application.domain.BuilderEvent;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureCommand;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureService;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component("pl.app.building.village_infrastructure.adapter.in.BuilderEventListener")
@RequiredArgsConstructor
class BuildingConstructionFinishedPolicy {
    private final Logger logger = LoggerFactory.getLogger(BuildingConstructionFinishedPolicy.class);
    private final VillageInfrastructureService service;

    @KafkaListener(
            id = "construct-finished--event-listener--village_infrastructure",
            groupId = "${app.kafka.consumer.group-id}--village_infrastructure",
            topics = "${app.kafka.topic.construct-finished.name}"
    )
    public void constructFinishedEvent(ConsumerRecord<ObjectId, BuilderEvent.ConstructFinishedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        service.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(event.getVillageId(), event.getType(), event.getToLevel()))
                .subscribe();
    }

}
