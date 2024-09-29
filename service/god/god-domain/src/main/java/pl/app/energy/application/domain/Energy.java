package pl.app.energy.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Getter
@Document(collection = "energy")
public class Energy {
    @Id
    private ObjectId id;
    private ObjectId godId;
    private Long currentEnergy;
    private Long maxEnergy = 100L;
    /*
     * REGENERATION
     * */
    private Duration energyRegenInterval = Duration.of(1, ChronoUnit.MINUTES);
    private Instant lastUpdate;

    public Energy(ObjectId godId) {
        this.id = ObjectId.get();
        this.godId = godId;
        this.currentEnergy = maxEnergy;
    }

    public long regenerateEnergy() {
        if (Objects.equals(maxEnergy, currentEnergy)) {
            lastUpdate = Instant.now();
            return 0;
        }
        var energyRegenCount = calculateEnergyRegenCount();
        addEnergy(energyRegenCount);
        lastUpdate = lastUpdate.plus(energyRegenInterval.multipliedBy(energyRegenCount));
        return energyRegenCount;
    }

    public void addEnergy(Long amount) {
        currentEnergy = currentEnergy + amount;
        if (currentEnergy > maxEnergy) {
            currentEnergy = maxEnergy;
        }
    }

    public void subtractEnergy(Long amount) {
        if (amount < 0) {
            throw new EnergyException.InvalidAmountException();
        }
        if (currentEnergy - amount < 0) {
            throw new EnergyException.InsufficientEnergyException();
        }
        currentEnergy = currentEnergy - amount;
    }

    private long calculateEnergyRegenCount() {
        Duration timeElapsed = Duration.between(lastUpdate, Instant.now());
        return timeElapsed.toMillis() / energyRegenInterval.toMillis();
    }
}
