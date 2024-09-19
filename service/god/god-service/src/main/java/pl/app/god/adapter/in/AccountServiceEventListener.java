package pl.app.god.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.account.application.domain.AccountEvent;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god.application.port.in.GodService;

@Component
@RequiredArgsConstructor
class AccountServiceEventListener {
    private final Logger logger = LoggerFactory.getLogger(AccountServiceEventListener.class);
    private final GodService godService;

    @KafkaListener(
            id = "account-created-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.account-created.name}"
    )
        // TODO probably to remove, god should be created manually
    void createGod(ConsumerRecord<ObjectId, AccountEvent.AccountCreatedEvent> record) {
//        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
//        final var event = record.value();
//        var command = new GodCommand.CreateGodCommand(
//                event.getAccountId(),
//                event.getNickName()
//        );
//        godService.create(command).block();
    }
}
