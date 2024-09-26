package pl.app.equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.battle.application.domain.BattleEvent;
import pl.app.equipment.application.port.in.EquipmentCommand;
import pl.app.equipment.application.port.in.EquipmentService;
import pl.app.item.application.port.in.ItemCommand;
import pl.app.item.application.port.in.ItemService;
import pl.app.tower_attack.application.domain.TowerAttackEvent;

@Component
@RequiredArgsConstructor
class BattleServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(BattleServiceEventListener.class);
    private final EquipmentService equipmentService;
    private final ItemService itemService;

    @KafkaListener(
            id = "battle-ended-event-listener--equipment",
            groupId = "${app.kafka.consumer.group-id}--equipment",
            topics = "${app.kafka.topic.battle-ended.name}"
    )
    void addMoney(ConsumerRecord<ObjectId, BattleEvent.BattleEndedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        event.getCharacterResults()
                .forEach(characterResult -> {
                    characterResult.getLoot().getItems().forEach(item -> {
                        var createItemCommand = new ItemCommand.CreateItemCommand(
                                item.getItemTemplateId(),
                                1 //TODO
                        );
                        itemService.createItems(createItemCommand).flatMap(createdItem -> {
                            var addItemToGodEquipmentCommand = new EquipmentCommand.AddItemToEquipmentCommand(
                                    characterResult.getGodId(),
                                    createdItem.getId()
                            );
                            return equipmentService.addItemToEquipment(addItemToGodEquipmentCommand);
                        }).block();
                    });
                });
    }

    @KafkaListener(
            id = "tower-attack-ended-event-listener--equipment",
            groupId = "${app.kafka.consumer.group-id}--equipment",
            topics = "${app.kafka.topic.tower-attack-ended.name}"
    )
    void addMoney2(ConsumerRecord<ObjectId, TowerAttackEvent.TowerAttackEndedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        event.getCharacterResults()
                .forEach(characterResult -> {
                    characterResult.getLoot().getItems().forEach(item -> {
                        var createItemCommand = new ItemCommand.CreateItemCommand(
                                item.getItemTemplateId(),
                                1 //TODO
                        );
                        itemService.createItems(createItemCommand).flatMap(createdItem -> {
                            var addItemToGodEquipmentCommand = new EquipmentCommand.AddItemToEquipmentCommand(
                                    characterResult.getGodId(),
                                    createdItem.getId()
                            );
                            return equipmentService.addItemToEquipment(addItemToGodEquipmentCommand);
                        }).block();
                    });
                });
    }
}
