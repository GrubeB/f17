package pl.app.god_equipment.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.item.application.domain.Item;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Document(collection = "god_equipment")
public class GodEquipment {
    @Id
    private ObjectId id;
    private ObjectId godId;

    @DBRef
    private Set<Item> items;

    @DocumentReference
    private Set<CharacterGear> characterGears;

    @SuppressWarnings("unused")
    public GodEquipment() {
    }

    public GodEquipment(ObjectId godId, Set<Item> items) {
        this.id = ObjectId.get();
        this.godId = godId;
        this.items = items;
        this.characterGears = new LinkedHashSet<>();
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public Item removeItemById(ObjectId itemId, String itemType) {
        Item item = getItemByIdOrThrow(itemId, itemType);
        this.items.removeIf(i -> i.getId().equals(itemId));
        return item;
    }

    public Item removeItemFromCharacterGear(ObjectId characterId, String slotName) {
        GearSlot slot = GearSlot.valueOf(slotName);
        CharacterGear characterGear = getCharacterGearByIdOrThrow(characterId);
        return characterGear.removeItem(slot);
    }

    public Item setItemToCharacterGear(ObjectId characterId, String slotName, ObjectId itemId, String itemType) {
        GearSlot slot = GearSlot.valueOf(slotName);
        Item item = getItemByIdOrThrow(itemId, itemType);
        CharacterGear characterGear = getCharacterGearByIdOrThrow(characterId);
        characterGear.setItem(item, slot);
        return item;
    }

    public CharacterGear removeCharacterGearByCharacterId(ObjectId characterId) {
        CharacterGear characterGear = getCharacterGearByIdOrThrow(characterId);
        this.characterGears.remove(characterGear);
        return characterGear;
    }

    public CharacterGear addCharacterGear(CharacterGear characterGear) {
        this.characterGears.add(characterGear);
        return characterGear;
    }


    private CharacterGear getCharacterGearByIdOrThrow(ObjectId characterId) {
        return getCharacterGearById(characterId)
                .orElseThrow(() -> GodEquipmentException.NotFoundCharacterGearException.fromCharacterId(characterId.toHexString()));
    }

    public Optional<CharacterGear> getCharacterGearById(ObjectId characterId) {
        return this.characterGears.stream().filter(ch -> ch.getCharacterId().equals(characterId)).findAny();
    }

    private Item getItemByIdOrThrow(ObjectId itemId, String itemType) {
        return getItemById(itemId, itemType).orElseThrow(() -> GodEquipmentException.NotFoundGodEquipmentException.fromId(itemId.toHexString()));
    }

    private Optional<Item> getItemById(ObjectId itemId, String itemType) {
        return this.items.stream().filter(i -> i.getId().equals(itemId) && i.getType().name().equals(itemType))
                .findAny();
    }
}
