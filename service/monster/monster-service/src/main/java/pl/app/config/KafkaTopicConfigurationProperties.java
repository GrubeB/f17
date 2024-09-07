package pl.app.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.kafka.topic")
@Setter
@Getter
@NoArgsConstructor
public class KafkaTopicConfigurationProperties {
    private Topic monsterCreated;
    private Topic monsterRemoved;
    private Topic monsterUpdated;

    private Topic monsterTemplateCreated;
    private Topic monsterTemplateRemoved;
    private Topic monsterTemplateUpdated;

    private Topic towerLevelCreated;
    private Topic towerLevelRemoved;
    private Topic towerLevelUpdated;

    @Setter
    @Getter
    public static class Topic {
        private String name;
        private Integer partitions;
        private Boolean dtlTopic;

        public Topic() {
            this.name = "NAME_NOT_CONFIGURED";
            this.partitions = 1;
            this.dtlTopic = true;
        }
    }
}