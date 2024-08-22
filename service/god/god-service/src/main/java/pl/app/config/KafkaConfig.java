package pl.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@Configuration
@EnableKafka
@PropertySource("classpath:kafka.properties")
public class KafkaConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);
    @Value("${app.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Configuration
    class TopicConfiguration {
        @Bean
        KafkaAdmin admin() {
            Map<String, Object> configs = new HashMap<>();
            configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            return new KafkaAdmin(configs);
        }

        @Bean
        KafkaAdmin.NewTopics createTopics(KafkaTopicConfigurationProperties topicNames) {
            NewTopic[] array = Stream.of(
                    createTopicFromConfig(topicNames.getGodCreated()).stream()
            ).flatMap(Stream::sequential).toArray(NewTopic[]::new);
            return new KafkaAdmin.NewTopics(array);
        }

        private List<NewTopic> createTopicFromConfig(KafkaTopicConfigurationProperties.Topic topic) {
            NewTopic mainTopic = TopicBuilder.name(topic.getName()).partitions(topic.getPartitions()).compact().build();
            if (!topic.getDtlTopic()) {
                return Collections.singletonList(mainTopic);
            }
            return List.of(
                    mainTopic,
                    TopicBuilder.name(topic.getName() + ".DTL").partitions(topic.getPartitions()).compact().build()
            );
        }
    }

    @Configuration
    class ConsumerConfiguration {

        @Value("${app.kafka.consumer.client-id}")
        private String consumerClientId;

        @Bean
        public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<ObjectId, Object>> kafkaListenerContainerFactory(
                CommonErrorHandler commonErrorHandler,
                Deserializer<ObjectId> objectIdDeserializer,
                JsonDeserializer<Object> jsonDeserializer
        ) {
            ConsumerFactory<ObjectId, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfig(),
                    () -> objectIdDeserializer, () -> jsonDeserializer);
            ConcurrentKafkaListenerContainerFactory<ObjectId, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory);
            factory.setConcurrency(1);
            factory.getContainerProperties().setPollTimeout(3000);
            factory.setCommonErrorHandler(commonErrorHandler);
            return factory;
        }

        @Bean
        CommonErrorHandler commonErrorHandler(
                KafkaTemplate<ObjectId, Object> objectIdTemplate
        ) {
            Map<Class<?>, KafkaOperations<?, ?>> templates = new LinkedHashMap<>() {{
                put(Object.class, objectIdTemplate);
            }};
            DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(templates, new BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition>() {
                @Override
                public TopicPartition apply(ConsumerRecord<?, ?> consumerRecord, Exception e) {
                    final String topicName = consumerRecord.topic() + ".DTL";
                    logger.debug("send unprocessed event to: {}, event: {}, because of exception: {}", topicName, consumerRecord.value(), e.getCause().getMessage());
                    return new TopicPartition(topicName, 0);
                }
            });
            return new DefaultErrorHandler(recoverer, new FixedBackOff(3_000L, 1L));
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

    }

    @Configuration
    class ProducerConfiguration {
        @Value("${app.kafka.producer.client-id}")
        private String producerClientId;


        @Bean
        KafkaTemplate<ObjectId, Object> objectIdTemplate(
                Serializer<ObjectId> objectIdSerializer,
                JsonSerializer<Object> jsonSerializer
        ) {
            return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs(), () -> objectIdSerializer, () -> jsonSerializer));
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
}
