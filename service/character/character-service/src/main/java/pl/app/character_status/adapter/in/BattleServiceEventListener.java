package pl.app.character_status.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.battle.application.domain.BattleEvent;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character_status.application.domain.CharacterStatusType;
import pl.app.character_status.application.port.in.CharacterStatusCommand;
import pl.app.character_status.application.port.in.CharacterStatusService;
import pl.app.tower_attack.application.domain.TowerAttackEvent;

@Component("pl.app.character_status.adapter.in.BattleServiceEventListener")
@RequiredArgsConstructor
class BattleServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(BattleServiceEventListener.class);
    private final CharacterStatusService characterStatusService;

    @KafkaListener(
            id = "battle-ended-event-listener--character_status",
            groupId = "${app.kafka.consumer.group-id}--character_status",
            topics = "${app.kafka.topic.battle-ended.name}"
    )
    void changeStatus(ConsumerRecord<ObjectId, BattleEvent.BattleEndedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        event.getCharacterResults()
                .forEach(characterResult -> {
                    var command = new CharacterStatusCommand.ChangeCharacterStatusCommand(
                            characterResult.getCharacterId(),
                            CharacterStatusType.IDLE
                    );
                    characterStatusService.changeStatus(command).block();
                });
    }
    @KafkaListener(
            id = "tower-attack-ended-event-listener--character_status",
            groupId = "${app.kafka.consumer.group-id}--character_status",
            topics = "${app.kafka.topic.tower-attack-ended.name}"
    )
    void changeStatus2(ConsumerRecord<ObjectId, TowerAttackEvent.TowerAttackEndedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        event.getCharacterResults()
                .forEach(characterResult -> {
                    var command = new CharacterStatusCommand.ChangeCharacterStatusCommand(
                            characterResult.getCharacterId(),
                            CharacterStatusType.IDLE
                    );
                    characterStatusService.changeStatus(command).block();
                });
    }
    @KafkaListener(
            id = "tower-attack-started-event-listener--character_status",
            groupId = "${app.kafka.consumer.group-id}--character_status",
            topics = "${app.kafka.topic.tower-attack-started.name}"
    )
    void changeStatus3(ConsumerRecord<ObjectId, TowerAttackEvent.TowerAttackStartedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        event.getCharacterIds().forEach(characterId -> {
                    var command = new CharacterStatusCommand.ChangeCharacterStatusCommand(
                            characterId,
                            CharacterStatusType.ATTACKING
                    );
                    characterStatusService.changeStatus(command).block();
                });
    }
}
