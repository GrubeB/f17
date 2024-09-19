package pl.app.character.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.battle.application.domain.BattleEvent;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.application.port.in.CharacterLevelService;
import pl.app.tower_attack.application.domain.TowerAttackEvent;

@Component
@RequiredArgsConstructor
class BattleServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(BattleServiceEventListener.class);
    private final CharacterLevelService characterLevelService;
    @KafkaListener(
            id = "battle-ended-event-listener--character",
            groupId = "${app.kafka.consumer.group-id}--character",
            topics = "${app.kafka.topic.battle-ended.name}"
    )
    void addMoney(ConsumerRecord<ObjectId, BattleEvent.BattleEndedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        event.getCharacterResults()
                .forEach(characterResult -> {
                    var command = new CharacterCommand.AddExpCommand(
                            characterResult.getCharacterId(),
                            characterResult.getProgress().getExp()
                    );
                    characterLevelService.addExp(command).block();
                });
    }
    @KafkaListener(
            id = "tower-attack-ended-event-listener--character",
            groupId = "${app.kafka.consumer.group-id}--character",
            topics = "${app.kafka.topic.tower-attack-ended.name}"
    )
    void addMoney2(ConsumerRecord<ObjectId, TowerAttackEvent.TowerAttackEndedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        event.getCharacterResults()
                .forEach(characterResult -> {
                    var command = new CharacterCommand.AddExpCommand(
                            characterResult.getCharacterId(),
                            characterResult.getProgress().getExp()
                    );
                    characterLevelService.addExp(command).block();
                });
    }
}
