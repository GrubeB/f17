package pl.app.god_equipment.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.common.shared.model.ItemType;
import pl.app.gear.aplication.domain.GearSlot;
import pl.app.item.application.domain.Item;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Item removeItemById(ObjectId itemId) {
        Item item = getItemByIdOrThrow(itemId);
        this.items.removeIf(i -> i.getId().equals(itemId));
        return item;
    }

    public Item removeItemFromCharacterGear(ObjectId characterId,  GearSlot slot) {
        CharacterGear characterGear = getCharacterGearByIdOrThrow(characterId);
        return characterGear.removeItem(slot);
    }

    public Item setItemToCharacterGear(ObjectId characterId, GearSlot slot, ObjectId itemId) {
        Item item = getItemByIdOrThrow(itemId);
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

    public Set<Item> getOutfits(){
        return this.items.stream().filter(i -> ItemType.isOutfit(i.getType())).collect(Collectors.toSet());
    }
    public Set<Item> getAttachedOutfits(){
        return this.characterGears.stream().map(CharacterGear::getItems).flatMap(Set::stream).filter(i -> ItemType.isOutfit(i.getType())).collect(Collectors.toSet());
    }
    public Set<Item> getUnattachedOutfits(){
        Set<Item> attachedOutfits = getAttachedOutfits();
        return  getOutfits().stream().filter(i -> attachedOutfits.stream().noneMatch(au -> au.getId().equals(i.getId()))).collect(Collectors.toSet());
    }
    public Set<Item> getWeapons(){
        return this.items.stream().filter(i -> ItemType.isWeapon(i.getType())).collect(Collectors.toSet());
    }
    public Set<Item> getAttachedWeapons(){
        return this.characterGears.stream().map(CharacterGear::getItems).flatMap(Set::stream).filter(i -> ItemType.isWeapon(i.getType())).collect(Collectors.toSet());
    }
    public Set<Item> getUnattachedWeapons(){
        Set<Item> attachedWeapons = getAttachedWeapons();
        return  getWeapons().stream().filter(i -> attachedWeapons.stream().noneMatch(au -> au.getId().equals(i.getId()))).collect(Collectors.toSet());
    }
    public CharacterGear getCharacterGearByIdOrThrow(ObjectId characterId) {
        return getCharacterGearById(characterId)
                .orElseThrow(() -> GodEquipmentException.NotFoundCharacterGearException.fromCharacterId(characterId.toHexString()));
    }

    public Optional<CharacterGear> getCharacterGearById(ObjectId characterId) {
        return this.characterGears.stream().filter(ch -> ch.getCharacterId().equals(characterId)).findAny();
    }

    private Item getItemByIdOrThrow(ObjectId itemId) {
        return getItemById(itemId).orElseThrow(() -> GodEquipmentException.NotFoundGodEquipmentException.fromId(itemId.toHexString()));
    }

    private Optional<Item> getItemById(ObjectId itemId) {
        return this.items.stream().filter(i -> i.getId().equals(itemId))
                .findAny();
    }
}
