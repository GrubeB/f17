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
    private Topic outfitTemplateCreated;
    private Topic outfitTemplateUpdated;
    private Topic outfitTemplateDeleted;
    private Topic outfitCreated;
    private Topic outfitDeleted;
    private Topic weaponTemplateCreated;
    private Topic weaponTemplateUpdated;
    private Topic weaponTemplateDeleted;
    private Topic weaponCreated;
    private Topic weaponDeleted;

    private Topic godEquipmentCreated;
    private Topic characterGearCreated;
    private Topic characterGearRemoved;
    private Topic characterGearAddedToGodEquipment;
    private Topic characterGearRemovedFromGodEquipment;
    private Topic godEquipmentItemAdded;
    private Topic godEquipmentItemRemoved;
    private Topic characterItemSet;
    private Topic characterItemRemoved;

    private Topic traderCreated;
    private Topic traderItemsRenewed;
    private Topic godBoughtItem;
    private Topic godSoldItem;

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
