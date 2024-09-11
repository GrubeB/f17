package pl.app.gear.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Statistics;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GearDto implements Serializable {
    private ObjectId id; // domainObjectId
    private OutfitDto helmet;
    private OutfitDto armor;
    private OutfitDto gloves;
    private OutfitDto boots;
    private OutfitDto belt;
    private OutfitDto ring;
    private OutfitDto amulet;
    private OutfitDto talisman;

    private WeaponDto leftHand;
    private WeaponDto rightHand;

    public Statistics getStatistic() {
        Statistics statistics = new Statistics();
        if (Objects.nonNull(helmet)) {
            statistics.add(helmet.getStatistic());
        }
        if (Objects.nonNull(armor)) {
            statistics.add(armor.getStatistic());
        }
        if (Objects.nonNull(gloves)) {
            statistics.add(gloves.getStatistic());
        }

        if (Objects.nonNull(boots)) {
            statistics.add(boots.getStatistic());
        }
        if (Objects.nonNull(belt)) {
            statistics.add(belt.getStatistic());
        }
        if (Objects.nonNull(ring)) {
            statistics.add(ring.getStatistic());
        }

        if (Objects.nonNull(amulet)) {
            statistics.add(amulet.getStatistic());
        }
        if (Objects.nonNull(talisman)) {
            statistics.add(talisman.getStatistic());
        }
        if (Objects.nonNull(leftHand)) {
            statistics.add(leftHand.getStatistic());
        }

        if (Objects.nonNull(leftHand)) {
            statistics.add(leftHand.getStatistic());
        }

        return statistics;
    }
}