package pl.app.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(
            id = "thing3",
            groupId = "spring-boot-app-group-id",
            topicPartitions = @TopicPartition(
                    topic = "hobbit", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0", seekPosition = "BEGINNING")
            )
    )
    public void consume(ConsumerRecord<Integer, String> record) {
        logger.info("received " + record.key() + " with value: " + record.value());
    }
}
