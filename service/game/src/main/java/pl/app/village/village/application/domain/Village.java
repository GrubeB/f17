package pl.app.village.village.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@Document(collection = "village")
public class Village {
    private ObjectId id;
    private VillageType type;
    private ObjectId ownerId; // playerId or null
    private VillageLoyalty villageLoyalty;

    public Village() {
    }

    public Village(VillageType type, ObjectId ownerId) {
        this.id = ObjectId.get();
        this.type = type;
        this.ownerId = ownerId;
        this.villageLoyalty = new VillageLoyalty();
    }

    @Getter
    @Setter
    public static class VillageLoyalty {
        public static final Integer LOYALTY_MAX = 100;
        public static final Integer LOYALTY_MIN = -10;
        public static final Integer LOYALTY_AFTER_CONQUERING = 25;
        public static final Integer LOYALTY_PER_HOUR = 1;
        private Integer loyalty;
        private Instant lastLoyaltyRefresh;

        public VillageLoyalty() {
            this.loyalty = LOYALTY_MAX;
            this.lastLoyaltyRefresh = Instant.now();
        }

        public void reset() {
            this.loyalty = LOYALTY_AFTER_CONQUERING;
            this.lastLoyaltyRefresh = Instant.now();
        }

        public void refreshLoyalty() {
            if (loyalty >= LOYALTY_MAX) {
                lastLoyaltyRefresh = Instant.now();
                return;
            }
            double loyaltyPerSecond = (double) LOYALTY_PER_HOUR / 3_600;
            Duration timeElapsed = Duration.between(lastLoyaltyRefresh, Instant.now());
            int totalToAdd = (int) (loyaltyPerSecond * timeElapsed.toSeconds());
            Duration actualTimeToAdd = Duration.ofSeconds(totalToAdd * 3_600L);
            lastLoyaltyRefresh = lastLoyaltyRefresh.plus(actualTimeToAdd);
            loyalty = Math.max(loyalty + totalToAdd, LOYALTY_MAX);
        }

        public void subtractLoyalty(Integer number) {
            if (loyalty >= LOYALTY_MAX) {
                lastLoyaltyRefresh = Instant.now();
            }
            loyalty = Math.max(loyalty - number, LOYALTY_MIN);
        }
    }
}
