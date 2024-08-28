package pl.app.god.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god.application.port.in.GodMoneyService;
import pl.app.trader.application.domain.TraderEvent;

@Component("pl.app.god.adapter.in.ItemServiceEventListener")
@RequiredArgsConstructor
class ItemServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(ItemServiceEventListener.class);
    private final GodMoneyService godMoneyService;

    @KafkaListener(
            id = "god-sold-item-event-listener--god",
            groupId = "${app.kafka.consumer.group-id}--god",
            topics = "${app.kafka.topic.god-sold-item.name}"
    )
    public void createGod(ConsumerRecord<ObjectId, TraderEvent.GodSoldItemEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new GodCommand.AddMoneyCommand(
               event.getGodId(), event.getMoney()
        );
        godMoneyService.addMoney(command).block();
    }
}
