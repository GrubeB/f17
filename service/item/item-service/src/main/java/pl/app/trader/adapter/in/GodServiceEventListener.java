package pl.app.trader.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.god.application.domain.GodEvent;
import pl.app.trader.application.port.in.TraderCommand;
import pl.app.trader.application.port.in.TraderService;

@Component("pl.app.trader.adapter.in.GodServiceEventListener")
@RequiredArgsConstructor
class GodServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(GodServiceEventListener.class);
    private final TraderService traderService;

    @KafkaListener(
            id = "god-created-event-listener--trader",
            groupId = "${app.kafka.consumer.group-id}--trader",
            topics = "${app.kafka.topic.god-created.name}"
    )
    public void createGod(ConsumerRecord<ObjectId, GodEvent.GodCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new TraderCommand.CrateTraderCommand(
                event.getGodId()
        );
        traderService.create(command).block();
    }
}
