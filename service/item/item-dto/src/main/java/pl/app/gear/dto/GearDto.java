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
            statistics.mergeWith(helmet.getStatistic());
        }
        if (Objects.nonNull(armor)) {
            statistics.mergeWith(armor.getStatistic());
        }
        if (Objects.nonNull(gloves)) {
            statistics.mergeWith(gloves.getStatistic());
        }

        if (Objects.nonNull(boots)) {
            statistics.mergeWith(boots.getStatistic());
        }
        if (Objects.nonNull(belt)) {
            statistics.mergeWith(belt.getStatistic());
        }
        if (Objects.nonNull(ring)) {
            statistics.mergeWith(ring.getStatistic());
        }

        if (Objects.nonNull(amulet)) {
            statistics.mergeWith(amulet.getStatistic());
        }
        if (Objects.nonNull(talisman)) {
            statistics.mergeWith(talisman.getStatistic());
        }
        if (Objects.nonNull(leftHand)) {
            statistics.mergeWith(leftHand.getStatistic());
        }

        if (Objects.nonNull(leftHand)) {
            statistics.mergeWith(leftHand.getStatistic());
        }

        return statistics;
    }
}