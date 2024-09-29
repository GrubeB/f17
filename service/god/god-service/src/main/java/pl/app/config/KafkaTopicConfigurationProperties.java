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
    private Topic godCreated;
    private Topic moneyAdded;
    private Topic moneySubtracted;

    private Topic recruitmentAnnouncementPosted;

    private Topic godTemplateCreated;
    private Topic godTemplateUpdated;
    private Topic godTemplateDeleted;

    private Topic energyCreated;
    private Topic energyAdded;
    private Topic energySubtracted;
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
