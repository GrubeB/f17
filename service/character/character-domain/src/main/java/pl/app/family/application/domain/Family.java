package pl.app.family.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.character.application.domain.Character;
import pl.app.character.application.domain.CharacterException;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Document(collection = "god_family")
public class Family {
    @Id
    private ObjectId id;
    private ObjectId godId;

    @DocumentReference
    private Set<Character> characters;

    @SuppressWarnings("unused")
    public Family() {
    }

    public Family(ObjectId godId) {
        this.id = ObjectId.get();
        this.godId = godId;
        this.characters = new LinkedHashSet<>();
    }

    public Character addCharacter(Character character) {
        Optional<Character> characterById = getCharacterById(character.getId());
        if(characterById.isPresent()){
            throw FamilyException.CharacterBelongsToFamilyException.fromId(character.getId().toHexString());
        }
        this.characters.add(character);
        return character;
    }

    public Character removeCharacterById(ObjectId characterId) {
        Character characterToRemove = this.getCharacterByIdOrThrow(characterId);
        this.characters.remove(characterToRemove);
        return characterToRemove;
    }


    private Character getCharacterByIdOrThrow(ObjectId characterId) {
        return getCharacterById(characterId)
                .orElseThrow(() -> CharacterException.NotFoundCharacterException.fromId(characterId.toHexString()));
    }

    private Optional<Character> getCharacterById(ObjectId characterId) {
        return this.characters.stream().filter(ch -> ch.getId().equals(characterId))
                .findAny();
    }
}
