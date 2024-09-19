package pl.app.god.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.account.application.domain.AccountEvent;
import pl.app.battle.application.domain.BattleEvent;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god.application.port.in.GodMoneyService;
import pl.app.god.application.port.in.GodService;
import pl.app.tower_attack.application.domain.TowerAttackEvent;

@Component
@RequiredArgsConstructor
class BattleServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(BattleServiceEventListener.class);
    private final GodMoneyService godMoneyService;

    @KafkaListener(
            id = "battle-ended-event-listener--god",
            groupId = "${app.kafka.consumer.group-id}--god",
            topics = "${app.kafka.topic.battle-ended.name}"
    )
    void addMoney(ConsumerRecord<ObjectId, BattleEvent.BattleEndedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        event.getCharacterResults()
                .forEach(characterResult -> {
                    var command = new GodCommand.AddMoneyCommand(
                            characterResult.getGodId(),
                            characterResult.getLoot().getMoney()
                    );
                    godMoneyService.addMoney(command).block();
                });
    }

    @KafkaListener(
            id = "tower-attack-ended-event-listener--god",
            groupId = "${app.kafka.consumer.group-id}--god",
            topics = "${app.kafka.topic.tower-attack-ended.name}"
    )
    void addMoney2(ConsumerRecord<ObjectId, TowerAttackEvent.TowerAttackEndedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        event.getCharacterResults()
                .forEach(characterResult -> {
                    var command = new GodCommand.AddMoneyCommand(
                            characterResult.getGodId(),
                            characterResult.getLoot().getMoney()
                    );
                    godMoneyService.addMoney(command).block();
                });
    }
}
