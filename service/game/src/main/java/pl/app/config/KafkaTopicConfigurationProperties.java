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
    private Topic test;

    // building
    private Topic villageInfrastructureCreated;
    private Topic villageInfrastructureBuildingLevelUp;
    private Topic villageInfrastructureBuildingLevelDown;

    // resource
    private Topic villageResourceCreated;
    private Topic resourceAdded;
    private Topic resourceSubtracted;

    // village
    private Topic villageCreated;

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
