package pl.app.common.shared.model;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ItemType {
    AXE,
    CLUB,
    SWORD,
    RODS,
    WANDS,
    THROWING_WEAPONS,
    BOWS,
    CROSSBOWS,

    HELMET,
    ARMOR,
    GLOVES,
    BOOTS,
    BELT,
    RING,
    AMULET,
    TALISMAN,

    POTION;

    public static Set<ItemType> getWeaponTypes(){
        return Set.of(
                ItemType.AXE,
                ItemType.CLUB,
                ItemType.SWORD,
                ItemType.RODS,
                ItemType.WANDS,
                ItemType.THROWING_WEAPONS,
                ItemType.CROSSBOWS,
                ItemType.BOWS
        );
    }
    public static Set<ItemType> getOutfitTypes(){
        return Set.of(
                ItemType.HELMET,
                ItemType.ARMOR,
                ItemType.GLOVES,
                ItemType.BOOTS,
                ItemType.BELT,
                ItemType.RING,
                ItemType.AMULET,
                ItemType.TALISMAN
        );
    }
    public static Set<ItemType> getPotionTypes(){
        return Set.of(
                ItemType.POTION
        );
    }
    public static Set<ItemType> geTypes(){
        return Stream.of(ItemType.values()).collect(Collectors.toSet());
    }
    public static boolean isWeapon(ItemType type) {
        return ItemType.AXE.equals(type)
                || ItemType.CLUB.equals(type)
                || ItemType.SWORD.equals(type)
                || ItemType.RODS.equals(type)
                || ItemType.WANDS.equals(type)
                || ItemType.THROWING_WEAPONS.equals(type)
                || ItemType.CROSSBOWS.equals(type)
                || ItemType.BOWS.equals(type);
    }
    public static boolean isOutfit(ItemType type) {
        return ItemType.HELMET.equals(type)
                || ItemType.ARMOR.equals(type)
                || ItemType.GLOVES.equals(type)
                || ItemType.BOOTS.equals(type)
                || ItemType.BELT.equals(type)
                || ItemType.RING.equals(type)
                || ItemType.AMULET.equals(type)
                || ItemType.TALISMAN.equals(type);
    }

    public static boolean isPotion(ItemType type) {
        return ItemType.POTION.equals(type);
    }
}
