package pl.app.resource.village_resource.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.building.village_infrastructure.application.domain.VillageInfrastructureEvent;
import pl.app.resource.share.Resource;
import pl.app.resource.share.ResourceType;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component("pl.app.resource.village_resource.adapter.in.VillageInfrastructureEventListener")
@RequiredArgsConstructor
class VillageInfrastructureEventListener {
    private final Logger logger = LoggerFactory.getLogger(VillageInfrastructureEventListener.class);
    private final VillageResourceService villageResourceService;

    @KafkaListener(
            id = "village-infrastructure-building-level-up-event-listener--village-resource",
            groupId = "${app.kafka.consumer.group-id}--village-resource",
            topics = "${app.kafka.topic.village-infrastructure-building-level-up.name}"
    )
    public void villageInfrastructureBuildingLevelUpEvent(ConsumerRecord<ObjectId, VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        if (record.value() instanceof VillageInfrastructureEvent.VillageInfrastructureFarmBuildingLevelUpEvent event) {
            villageResourceService.add(new VillageResourceCommand.AddResourceCommand(
                    event.getVillageId(),
                    Resource.of(event.getProvisionsUp(), ResourceType.PROVISION)
            )).subscribe();
        }
    }

    @KafkaListener(
            id = "village-infrastructure-building-level-down-event-listener--village-resource",
            groupId = "${app.kafka.consumer.group-id}--village-resource",
            topics = "${app.kafka.topic.village-infrastructure-building-level-down.name}"
    )
    public void villageInfrastructureBuildingLevelDownEvent(ConsumerRecord<ObjectId, VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        if (record.value() instanceof VillageInfrastructureEvent.VillageInfrastructureFarmBuildingLevelDownEvent event) {
            villageResourceService.subtract(new VillageResourceCommand.SubtractResourceCommand(
                    event.getVillageId(),
                    Resource.of(event.getProvisionsDown(), ResourceType.PROVISION)
            )).subscribe();
        }
    }
}
