package pl.app.loot.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.loot.aplication.domain.Loot;
import pl.app.loot.application.port.in.LootCommand;
import pl.app.loot.application.port.in.LootService;
import pl.app.monster.application.domain.MonsterEvent;

@Component("pl.app.loot.adapter.in.MonsterServiceEventListener")
@RequiredArgsConstructor
class MonsterServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(MonsterServiceEventListener.class);
    private final LootService lootService;

    @KafkaListener(
            id = "monster-created-event-listener--loot",
            groupId = "${app.kafka.consumer.group-id}--loot",
            topics = "${app.kafka.topic.monster-created.name}"
    )
    void monsterCreatedEvent(ConsumerRecord<ObjectId, MonsterEvent.MonsterCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        final var monsterId = event.getId();
        lootService.crate(new LootCommand.CreateLootCommand(monsterId, Loot.LootDomainObjectType.MONSTER)).block();

    }

    @KafkaListener(
            id = "monster-removed-event-listener--loot",
            groupId = "${app.kafka.consumer.group-id}--loot",
            topics = "${app.kafka.topic.monster-removed.name}"
    )
    void monsterRemovedEvent(ConsumerRecord<ObjectId, MonsterEvent.MonsterRemovedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        final var monsterId = event.getId();
        lootService.remove(new LootCommand.RemoveLootCommand(monsterId, Loot.LootDomainObjectType.MONSTER)).block();
    }
}
