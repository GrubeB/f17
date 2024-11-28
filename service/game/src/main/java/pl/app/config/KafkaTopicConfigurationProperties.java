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

    // army
    private Topic recruiterCreated;
    private Topic recruitRequestAdded;
    private Topic recruitRequestStarted;
    private Topic recruitRequestFinished;
    private Topic recruitRequestCanceled;
    private Topic recruitRequestRejected;

    private Topic villageArmyCreated;
    private Topic unitsAdded;
    private Topic unitsSubtracted;
    private Topic unitsBlocked;
    private Topic unitsUnlocked;
    private Topic villageSupportAdded;
    private Topic villageSupportWithdraw;

    // attack
    private Topic attackStarted;

    // building
    private Topic villageInfrastructureCreated;
    private Topic villageInfrastructureBuildingLevelUp;
    private Topic villageInfrastructureBuildingLevelDown;
    private Topic builderCreated;
    private Topic constructAdded;
    private Topic constructStarted;
    private Topic constructFinished;
    private Topic constructRejected;
    private Topic constructCanceled;

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


    // village
    private Topic villageCreated;
    private Topic villageConquered;

    // loyalty
    private Topic villageLoyaltyCreated;
    private Topic loyaltyAdded;
    private Topic loyaltySubtracted;
    private Topic loyaltyReset;

    // village effect
    private Topic villageEffectCreated;
    private Topic villageEffectStarted;
    private Topic villageEffectExpired;
    private Topic villageEffectRejected;

    public List<String> getAllTopicNames() {
        return getAllTopics().stream()
                .map(t -> t.getDtlTopic() ? List.of(t.getName(), t.getName() + ".DTL") : List.of(t.getName()))
                .flatMap(List::stream)
                .toList();
    }

    public List<Topic> getAllTopics() {
        return List.of(
                test,
                // army
                recruiterCreated,
                recruitRequestAdded,
                recruitRequestStarted,
                recruitRequestFinished,
                recruitRequestCanceled,
                recruitRequestRejected,

                villageArmyCreated,
                unitsAdded,
                unitsSubtracted,
                unitsBlocked,
                unitsUnlocked,
                villageSupportAdded,
                villageSupportWithdraw,

                // building
                villageInfrastructureCreated,
                villageInfrastructureBuildingLevelUp,
                villageInfrastructureBuildingLevelDown,

                builderCreated,
                constructAdded,
                constructStarted,
                constructFinished,
                constructRejected,
                constructCanceled,

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

                // village
                villageCreated,
                villageConquered,
                // loyalty
                villageLoyaltyCreated,
                loyaltyAdded,
                loyaltySubtracted,
                loyaltyReset,

                villageEffectCreated,
                villageEffectStarted,
                villageEffectExpired,
                villageEffectRejected
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
