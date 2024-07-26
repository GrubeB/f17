package pl.app.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;
import pl.app.comment.application.domain.CommentEvent;

@Component
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(
            id = "consumer-id",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.domain-object-created.name}"
    )
    public void consume(ConsumerRecord<ObjectId, Object> record) {
        logger.info("received " + record.partition() + ":" + record.offset() + " - " + record.key() + " with value: " + record.value());
    }
}
