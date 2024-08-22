package pl.app.account_equipment.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import pl.app.item.application.domain.Item;

@Getter
public class CharacterGear {
    private ObjectId characterId;

    @DBRef
    private Item helmet;
    @DBRef
    private Item armor;
    @DBRef
    private Item gloves;
    @DBRef
    private Item boots;
    @DBRef
    private Item belt;
    @DBRef
    private Item ring;
    @DBRef
    private Item amulet;
    @DBRef
    private Item talisman;

    @DBRef
    private Item leftHand;
    @DBRef
    private Item rightHand;

    @SuppressWarnings("unused")
    public CharacterGear() {
    }

    public CharacterGear(ObjectId characterId) {
        this.characterId = characterId;
    }

    public void setItem(Item item, GearSlot slot) {
        switch (slot) {
            case LEFT_HAND -> this.leftHand = item;
            case RIGHT_HAND -> this.rightHand = item;
            case HELMET -> this.helmet = item;
            case ARMOR -> this.armor = item;
            case GLOVES -> this.gloves = item;
            case BOOTS -> this.boots = item;
            case BELT -> this.belt = item;
            case RING -> this.ring = item;
            case AMULET -> this.amulet = item;
            case TALISMAN -> this.talisman = item;
        }
    }

    public Item removeItem(GearSlot slot) {
        Item item = switch (slot) {
            case LEFT_HAND -> this.leftHand;
            case RIGHT_HAND -> this.rightHand;
            case HELMET -> this.helmet;
            case ARMOR -> this.armor;
            case GLOVES -> this.gloves;
            case BOOTS -> this.boots;
            case BELT -> this.belt;
            case RING -> this.ring;
            case AMULET -> this.amulet;
            case TALISMAN -> this.talisman;
        };
        switch (slot) {
            case LEFT_HAND -> this.leftHand = null;
            case RIGHT_HAND -> this.rightHand = null;
            case HELMET -> this.helmet = null;
            case ARMOR -> this.armor = null;
            case GLOVES -> this.gloves = null;
            case BOOTS -> this.boots = null;
            case BELT -> this.belt = null;
            case RING -> this.ring = null;
            case AMULET -> this.amulet = null;
            case TALISMAN -> this.talisman = null;
        }
        return item;
    }
}
