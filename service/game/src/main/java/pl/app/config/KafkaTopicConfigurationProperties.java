package pl.app.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.kafka.topic")
@Setter
@Getter
@NoArgsConstructor
public class KafkaTopicConfigurationProperties {
    private Topic test;

    // attack
    private Topic attackStarted;

    // building
    private Topic villageInfrastructureCreated;
    private Topic villageInfrastructureBuildingLevelUp;
    private Topic villageInfrastructureBuildingLevelDown;
    private Topic builderCreated;
    private Topic constructAdded;
    private Topic constructRemoved;
    // inventory
    private Topic playerInventoryCreated;
    private Topic itemAdded;
    private Topic itemRemoved;
    private Topic itemUsed;

    // gold coin
    private Topic playerGoldCoinCreated;
    private Topic goldCoinAdded;
    // money
    private Topic playerMoneyCreated;
    private Topic moneyAdded;
    private Topic moneySubtracted;

    // player
    private Topic playerCreated;

    // resource
    private Topic villageResourceCreated;
    private Topic resourceAdded;
    private Topic resourceSubtracted;

    // unit
    private Topic recruiterCreated;
    private Topic recruitRequestAdded;
    private Topic recruitRequestRemoved;
    private Topic villageArmyCreated;
    private Topic unitsAdded;
    private Topic unitsSubtracted;

    // village
    private Topic villageCreated;
    // village effect
    private Topic villageEffectCreated;

    public List<String> getAllTopicNames() {
        return getAllTopics().stream()
                .map(t -> t.getDtlTopic() ? List.of(t.getName(), t.getName() + ".DTL") : List.of(t.getName()))
                .flatMap(List::stream)
                .toList();
    }

    public List<Topic> getAllTopics() {
        return List.of(
                test,
                // building
                villageInfrastructureCreated,
                villageInfrastructureBuildingLevelUp,
                villageInfrastructureBuildingLevelDown,
                builderCreated,
                constructAdded,
                constructRemoved,

                // inventory
                playerInventoryCreated,
                itemAdded,
                itemRemoved,
                itemUsed,

                // money
                playerMoneyCreated,
                moneyAdded,
                moneySubtracted,
                // player
                playerCreated,
                // resource
                villageResourceCreated,
                resourceAdded,
                resourceSubtracted,
                // unit
                recruiterCreated,
                recruitRequestAdded,
                recruitRequestRemoved,
                villageArmyCreated,
                unitsAdded,
                unitsSubtracted,
                // village
                villageCreated,
                villageEffectCreated
        );
    }

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
