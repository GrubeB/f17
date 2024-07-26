package pl.app.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.kafka.support.serializer.JsonDeserializer.REMOVE_TYPE_INFO_HEADERS;
import static org.springframework.kafka.support.serializer.JsonDeserializer.TRUSTED_PACKAGES;

@Configuration
@EnableKafka
@PropertySource("classpath:kafka.properties")
public class KafkaConfig {

    @Value("${app.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${app.kafka.producer.client-id}")
    private String producerClientId;
    @Value("${app.kafka.consumer.client-id}")
    private String consumerClientId;

    // ---------------------------------------TOPIC-CONFIGURATION---------------------------------------------
    @Bean
    KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    NewTopic domainObjectCreatedTopic(@Value("${app.kafka.topic.domain-object-created.name}")
                                      String domainObjectCreatedTopicName) {
        return TopicBuilder.name(domainObjectCreatedTopicName)
                .partitions(11)
                .compact()
                .build();
    }

    @Bean
    NewTopic commentContainerCreated(@Value("${app.kafka.topic.comment-container-created.name}")
                                     String commentContainerCreatedTopicName) {
        return TopicBuilder.name(commentContainerCreatedTopicName)
                .partitions(11)
                .compact()
                .build();
    }

    @Bean
    NewTopic commentAdded(@Value("${app.kafka.topic.comment-added.name}")
                          String commentAddedTopicName) {
        return TopicBuilder.name(commentAddedTopicName)
                .partitions(11)
                .compact()
                .build();
    }

    @Bean
    NewTopic commentUpdated(@Value("${app.kafka.topic.comment-updated.name}")
                            String commentUpdatedTopicName) {
        return TopicBuilder.name(commentUpdatedTopicName)
                .partitions(11)
                .compact()
                .build();
    }

    @Bean
    NewTopic commentDeleted(@Value("${app.kafka.topic.comment-deleted.name}")
                            String commentDeletedTopicName) {
        return TopicBuilder.name(commentDeletedTopicName)
                .partitions(11)
                .compact()
                .build();
    }

    // --------------------------------CONSUMER-CONFIGURATION----------------------------------------------------

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<ObjectId, Object>> kafkaListenerContainerFactory() {
        ConsumerFactory<ObjectId, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(
                consumerConfig(),
                () -> (Deserializer<ObjectId>) (topic, data) -> new ObjectId(data),
                () -> {
                    JsonDeserializer<Object> deserializer = new JsonDeserializer<>();
                    deserializer.setRemoveTypeHeaders(false);
                    deserializer.addTrustedPackages("*");
                    return deserializer;
                }
        );
        ConcurrentKafkaListenerContainerFactory<ObjectId, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerClientId);
        return props;
    }

    // --------------------------------PRODUCER-CONFIGURATION----------------------------------------------------
    @Bean
    KafkaTemplate<ObjectId, Object> objectIdTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
                producerConfigs(),
                () -> (Serializer<ObjectId>) (topic, data) -> data.toByteArray(),
                JsonSerializer::new
        ));
    }

    @Bean
    KafkaTemplate<byte[], Object> byteTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
                producerConfigs(),
                ByteArraySerializer::new,
                JsonSerializer::new
        ));
    }

    @Bean
    Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, producerClientId);
        return props;
    }
}
