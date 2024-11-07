package pl.app.village.village.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.village.village.application.domain.VillageEvent;
import pl.app.village.village.application.domain.VillageType;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;

@Component("pl.app.village.village.adapter.in.VillageEventListener")
@RequiredArgsConstructor
class VillageEventListener {
    private final Logger logger = LoggerFactory.getLogger(VillageEventListener.class);
    private final VillageService villageService;

    @KafkaListener(
            id = "village-created-event-listener--village",
            groupId = "${app.kafka.consumer.group-id}--village",
            topics = "${app.kafka.topic.village-created.name}"
    )
    public void villageInfrastructureBuildingLevelUpEvent(ConsumerRecord<ObjectId, VillageEvent.VillageCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        if (event.getVillageType() == VillageType.PLAYER) {
            villageService.crateBarbarianVillage(new VillageCommand.CreateBarbarianVillageCommand())
                    .then(villageService.crateBarbarianVillage(new VillageCommand.CreateBarbarianVillageCommand()))
                    .subscribe();
        }
    }
}
