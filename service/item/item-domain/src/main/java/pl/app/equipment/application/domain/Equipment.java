package pl.app.equipment.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.common.shared.model.ItemType;
import pl.app.gear.aplication.domain.Gear;
import pl.app.item.application.domain.Item;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Document(collection = "equipment")
public class Equipment {
    @Id
    private ObjectId id;
    private ObjectId godId;

    @DBRef
    private Set<Item> items;

    @DocumentReference
    private Set<Gear> characterGears;

    @SuppressWarnings("unused")
    public Equipment() {
    }

    public Equipment(ObjectId godId, Set<Item> items) {
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

    public Gear removeCharacterGearByCharacterId(ObjectId characterId) {
        Gear characterGear = getCharacterGearByIdOrThrow(characterId);
        this.characterGears.remove(characterGear);
        return characterGear;
    }

    public Gear addCharacterGear(Gear characterGear) {
        this.characterGears.add(characterGear);
        return characterGear;
    }

    public Set<Item> getOutfits(){
        return this.items.stream().filter(i -> ItemType.isOutfit(i.getType())).collect(Collectors.toSet());
    }
    public Set<Item> getAttachedOutfits(){
        return this.characterGears.stream().map(Gear::getItems).flatMap(Set::stream).filter(i -> ItemType.isOutfit(i.getType())).collect(Collectors.toSet());
    }
    public Set<Item> getUnattachedOutfits(){
        Set<Item> attachedOutfits = getAttachedOutfits();
        return  getOutfits().stream().filter(i -> attachedOutfits.stream().noneMatch(au -> au.getId().equals(i.getId()))).collect(Collectors.toSet());
    }
    public Set<Item> getWeapons(){
        return this.items.stream().filter(i -> ItemType.isWeapon(i.getType())).collect(Collectors.toSet());
    }
    public Set<Item> getAttachedWeapons(){
        return this.characterGears.stream().map(Gear::getItems).flatMap(Set::stream).filter(i -> ItemType.isWeapon(i.getType())).collect(Collectors.toSet());
    }
    public Set<Item> getUnattachedWeapons(){
        Set<Item> attachedWeapons = getAttachedWeapons();
        return  getWeapons().stream().filter(i -> attachedWeapons.stream().noneMatch(au -> au.getId().equals(i.getId()))).collect(Collectors.toSet());
    }
    public Gear getCharacterGearByIdOrThrow(ObjectId characterId) {
        return getCharacterGearById(characterId)
                .orElseThrow(() -> EquipmentException.NotFoundCharacterGearException.fromCharacterId(characterId.toHexString()));
    }

    public Optional<Gear> getCharacterGearById(ObjectId characterId) {
        return this.characterGears.stream().filter(ch -> ch.getDomainObjectId().equals(characterId)).findAny();
    }

    private Item getItemByIdOrThrow(ObjectId itemId) {
        return getItemById(itemId).orElseThrow(() -> EquipmentException.NotFoundGodEquipmentException.fromId(itemId.toHexString()));
    }

    private Optional<Item> getItemById(ObjectId itemId) {
        return this.items.stream().filter(i -> i.getId().equals(itemId))
                .findAny();
    }
}
