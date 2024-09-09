package pl.app.gear.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.aplication.domain.GearSlot;
import pl.app.gear.application.port.in.GearCommand;
import pl.app.gear.application.port.in.GearService;
import pl.app.item.application.port.in.ItemCommand;
import pl.app.item.application.port.in.ItemService;
import pl.app.monster.application.domain.MonsterEvent;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component("pl.app.gear.adapter.in.MonsterServiceEventListener")
@RequiredArgsConstructor
class MonsterServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(MonsterServiceEventListener.class);
    private final GearService gearService;
    private final ItemService itemService;

    @KafkaListener(
            id = "monster-created-event-listener--gear",
            groupId = "${app.kafka.consumer.group-id}--gear",
            topics = "${app.kafka.topic.monster-created.name}"
    )
    void monsterCreatedEvent(ConsumerRecord<ObjectId, MonsterEvent.MonsterCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        final var monsterId = event.getId();
        gearService.crate(new GearCommand.CreateGearCommand(monsterId, Gear.LootDomainObjectType.MONSTER))
                .then(createOutfit(event.getHelmetTemplateId(), GearSlot.HELMET, event.getLevel(), monsterId))
                .then(createOutfit(event.getArmorTemplateId(), GearSlot.ARMOR, event.getLevel(), monsterId))
                .then(createOutfit(event.getGlovesTemplateId(), GearSlot.GLOVES, event.getLevel(), monsterId))
                .then(createOutfit(event.getBootsTemplateId(), GearSlot.BOOTS, event.getLevel(), monsterId))
                .then(createOutfit(event.getBeltTemplateId(), GearSlot.BELT, event.getLevel(), monsterId))
                .then(createOutfit(event.getRingTemplateId(), GearSlot.RING, event.getLevel(), monsterId))
                .then(createOutfit(event.getAmuletTemplateId(), GearSlot.AMULET, event.getLevel(), monsterId))
                .then(createOutfit(event.getTalismanTemplateId(), GearSlot.TALISMAN, event.getLevel(), monsterId))
                .then(createWeapon(event.getLeftHandTemplateId(), GearSlot.LEFT_HAND, event.getLevel(), monsterId))
                .then(createWeapon(event.getRightHandTemplateId(), GearSlot.RIGHT_HAND, event.getLevel(), monsterId))
                .block();

    }

    private Mono<Gear> createWeapon(ObjectId templateId, GearSlot slot, Integer level, ObjectId monsterId) {
        if (Objects.isNull(templateId)) {
            return Mono.empty();
        }
        return Mono.defer(() -> itemService.createWeapon(new ItemCommand.CreateWeaponCommand(templateId, level))
                        .flatMap(item -> gearService.setItem(new GearCommand.SetItemCommand(monsterId, Gear.LootDomainObjectType.MONSTER, slot, item.getId()))))
                .onErrorComplete();
    }

    private Mono<Gear> createOutfit(ObjectId templateId, GearSlot slot, Integer level, ObjectId monsterId) {
        if (Objects.isNull(templateId)) {
            return Mono.empty();
        }
        return Mono.defer(() -> itemService.createOutfit(new ItemCommand.CreateOutfitCommand(templateId, level))
                        .flatMap(item -> gearService.setItem(new GearCommand.SetItemCommand(monsterId, Gear.LootDomainObjectType.MONSTER, slot, item.getId()))))
                .onErrorComplete();
    }

    @KafkaListener(
            id = "monster-removed-event-listener--gear",
            groupId = "${app.kafka.consumer.group-id}--gear",
            topics = "${app.kafka.topic.monster-removed.name}"
    )
    void monsterRemovedEvent(ConsumerRecord<ObjectId, MonsterEvent.MonsterRemovedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        final var monsterId = event.getId();
        gearService.remove(new GearCommand.RemoveGearCommand(monsterId, Gear.LootDomainObjectType.MONSTER)).block();
    }
}
