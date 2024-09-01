package pl.app.character_template.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.CharacterRace;

@Getter
@Setter
@Document(collection = "character_templates")
public class CharacterTemplate {
    @Id
    private ObjectId id;
    private String name;
    private String description;
    private CharacterRace race;
    private CharacterProfession profession;
    private String imageId;
    @SuppressWarnings("unused")
    public CharacterTemplate() {
    }

    public CharacterTemplate(String name, String description, CharacterRace race, CharacterProfession profession, String imageId) {
        this.id = ObjectId.get();
        this.name = name;
        this.description = description;
        this.race = race;
        this.profession = profession;
        this.imageId = imageId;
    }
}
