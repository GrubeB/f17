package pl.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Objects;

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
    NewTopic votingCreated(@Value("${app.kafka.topic.voting-created.name}")
                           String votingCreatedTopicName) {
        return TopicBuilder.name(votingCreatedTopicName)
                .partitions(1)
                .compact()
                .build();
    }

    @Bean
    NewTopic voteAdded(@Value("${app.kafka.topic.vote-added.name}")
                       String voteAddedTopicName) {
        return TopicBuilder.name(voteAddedTopicName)
                .partitions(1)
                .compact()
                .build();
    }

    @Bean
    NewTopic voteRemoved(@Value("${app.kafka.topic.vote-removed.name}")
                         String voteRemovedTopicName) {
        return TopicBuilder.name(voteRemovedTopicName)
                .partitions(1)
                .compact()
                .build();
    }

    @Bean
    NewTopic createVotingRequested(@Value("${app.kafka.topic.create-voting-requested.name}")
                                   String createVotingRequestedTopicName) {
        return TopicBuilder.name(createVotingRequestedTopicName)
                .partitions(1)
                .compact()
                .build();
    }

    @Bean
    NewTopic addVoteRequested(@Value("${app.kafka.topic.add-vote-requested.name}")
                              String addVoteRequestedTopicName) {
        return TopicBuilder.name(addVoteRequestedTopicName)
                .partitions(1)
                .compact()
                .build();
    }

    @Bean
    NewTopic removeVoteRequested(@Value("${app.kafka.topic.remove-vote-requested.name}")
                                 String removeVoteRequestedTopicName) {
        return TopicBuilder.name(removeVoteRequestedTopicName)
                .partitions(1)
                .compact()
                .build();
    }

    // --------------------------------CONSUMER-CONFIGURATION----------------------------------------------------

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<ObjectId, Object>> kafkaListenerContainerFactory(
            Deserializer<ObjectId> objectIdDeserializer,
            JsonDeserializer<Object> jsonDeserializer
    ) {
        ConsumerFactory<ObjectId, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfig(),
                () -> objectIdDeserializer, () -> jsonDeserializer);
        ConcurrentKafkaListenerContainerFactory<ObjectId, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    JsonDeserializer<Object> jsonDeserializer(ObjectMapper objectMapper) {
        JsonDeserializer<Object> deserializer = new JsonDeserializer<>(objectMapper);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        return deserializer;
    }

    @Bean
    Deserializer<ObjectId> objectIdDeserializer() {
        return new Deserializer<ObjectId>() {
            @Override
            public ObjectId deserialize(String topic, byte[] data) {
                if (Objects.isNull(data) || data.length == 0) {
                    return null;
                }
                return new ObjectId(data);
            }
        };
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
    KafkaTemplate<ObjectId, Object> objectIdTemplate(
            Serializer<ObjectId> objectIdSerializer,
            JsonSerializer<Object> jsonSerializer
    ) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs(), () -> objectIdSerializer, () -> jsonSerializer));
    }


    @Bean
    KafkaTemplate<byte[], Object> byteTemplate(
            JsonSerializer<Object> jsonSerializer
    ) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
                producerConfigs(),
                ByteArraySerializer::new,
                () -> jsonSerializer
        ));
    }

    @Bean
    JsonSerializer<Object> jsonSerializer(ObjectMapper objectMapper) {
        return new JsonSerializer<>(objectMapper);
    }

    @Bean
    Serializer<ObjectId> objectIdSerializer() {
        return new Serializer<ObjectId>() {
            @Override
            public byte[] serialize(String topic, ObjectId data) {
                if (Objects.isNull(data)) {
                    return new byte[0];
                }
                return data.toByteArray();
            }
        };
    }

    @Bean
    Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, producerClientId);
        return props;
    }
}
