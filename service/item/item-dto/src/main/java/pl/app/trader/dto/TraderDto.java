package pl.app.trader.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraderDto implements Serializable {
    private ObjectId godId;
    private Set<OutfitDto> outfits;
    private Set<WeaponDto> weapons;
}
