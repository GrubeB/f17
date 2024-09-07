package pl.app.gear.aplication.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import pl.app.common.shared.model.ItemType;
import pl.app.god_equipment.application.domain.GodEquipmentException;
import pl.app.item.application.domain.Item;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class Gear {
    @Id
    protected ObjectId id;
    @DBRef
    protected Item helmet;
    @DBRef
    protected Item armor;
    @DBRef
    protected Item gloves;
    @DBRef
    protected Item boots;
    @DBRef
    protected Item belt;
    @DBRef
    protected Item ring;
    @DBRef
    protected Item amulet;
    @DBRef
    protected Item talisman;

    @DBRef
    protected Item leftHand;
    @DBRef
    protected Item rightHand;

    public Gear() {
        this.id = ObjectId.get();
    }

    public Set<Item> getItems() {
        return Stream.of(helmet, armor, gloves, boots, belt, ring, amulet, talisman, leftHand, rightHand)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void setItem(Item item, GearSlot slot) {
        if (ItemType.isWeapon(item.getType())) {
            if (GearSlot.LEFT_HAND.equals(slot)) {
                this.leftHand = item;
            } else if (GearSlot.RIGHT_HAND.equals(slot)) {
                this.rightHand = item;
            }else {
                throw new GodEquipmentException.WrongSlotException();
            }
        } else if (ItemType.isOutfit(item.getType())) {
            if (GearSlot.HELMET.equals(slot) && ItemType.HELMET.equals(item.getType())) {
                this.helmet = item;
            } else if (GearSlot.ARMOR.equals(slot) && ItemType.ARMOR.equals(item.getType())) {
                this.armor = item;
            } else if (GearSlot.GLOVES.equals(slot) && ItemType.GLOVES.equals(item.getType())) {
                this.gloves = item;
            }else if (GearSlot.BOOTS.equals(slot) && ItemType.BOOTS.equals(item.getType())) {
                this.boots = item;
            }else if (GearSlot.BELT.equals(slot) && ItemType.BELT.equals(item.getType())) {
                this.belt = item;
            }else if (GearSlot.RING.equals(slot) && ItemType.RING.equals(item.getType())) {
                this.ring = item;
            }else if (GearSlot.AMULET.equals(slot) && ItemType.AMULET.equals(item.getType())) {
                this.amulet = item;
            }else if (GearSlot.TALISMAN.equals(slot) && ItemType.TALISMAN.equals(item.getType())) {
                this.talisman = item;
            }else {
                throw new GodEquipmentException.WrongSlotException();
            }
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
