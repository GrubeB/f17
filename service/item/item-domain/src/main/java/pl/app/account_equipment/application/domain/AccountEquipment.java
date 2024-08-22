package pl.app.account_equipment.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.item.application.domain.Item;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Document(collection = "account_equipment")
public class AccountEquipment {
    @Id
    private ObjectId id;
    private ObjectId accountId;

    @DBRef
    private Set<Item> items;

    private Set<CharacterGear> characterGears;

    @SuppressWarnings("unused")
    public AccountEquipment() {
    }

    public AccountEquipment(ObjectId accountId, Set<Item> items) {
        this.id = ObjectId.get();
        this.accountId = accountId;
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
        CharacterGear characterGear = getCharacterGearByIdOrCreate(characterId);
        return characterGear.removeItem(slot);
    }

    public Item setItemToCharacterGear(ObjectId characterId, String slotName, ObjectId itemId, String itemType) {
        GearSlot slot = GearSlot.valueOf(slotName);
        Item item = getItemByIdOrThrow(itemId, itemType);
        CharacterGear characterGear = getCharacterGearByIdOrCreate(characterId);
        characterGear.setItem(item, slot);
        return item;
    }

    /* HELP METHODS*/
    private CharacterGear getCharacterGearByIdOrCreate(ObjectId characterId) {
        Optional<CharacterGear> characterGearOptional = getCharacterGearById(characterId);
        if (characterGearOptional.isPresent()) {
            return characterGearOptional.get();
        }
        CharacterGear newCharacterGear = new CharacterGear(characterId);
        this.characterGears.add(newCharacterGear);
        return newCharacterGear;
    }

    private Optional<CharacterGear> getCharacterGearById(ObjectId characterId) {
        return this.characterGears.stream().filter(ch -> ch.getCharacterId().equals(characterId)).findAny();
    }

    private Item getItemByIdOrThrow(ObjectId itemId, String itemType) {
        return getItemById(itemId, itemType).orElseThrow(() -> AccountEquipmentException.NotFoundItemException.fromId(itemId.toHexString()));
    }

    private Optional<Item> getItemById(ObjectId itemId, String itemType) {
        return this.items.stream().filter(i -> i.getId().equals(itemId) && i.getType().name().equals(itemType))
                .findAny();
    }
}
