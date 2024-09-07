package pl.app.monster_template.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.common.shared.model.CharacterProfession;
import pl.app.common.shared.model.CharacterRace;
import pl.app.common.shared.model.Statistics;
import pl.app.gear.aplication.domain.TemplateGear;

@Getter
@Setter
@Document(collection = "monster_templates")
public class MonsterTemplate {
    @Id
    private ObjectId id;
    private String name;
    private String description;
    private CharacterRace race;
    private CharacterProfession profession;
    private String imageId;

    private Statistics base;
    private Statistics perLevel;

    private TemplateGear gear;

    @SuppressWarnings("unused")
    public MonsterTemplate() {
    }

    public MonsterTemplate(String name, String description, CharacterRace race, CharacterProfession profession, String imageId, Statistics base, Statistics perLevel, TemplateGear gear) {
        this.id= ObjectId.get();
        this.name = name;
        this.description = description;
        this.race = race;
        this.profession = profession;
        this.imageId = imageId;
        this.base = base;
        this.perLevel = perLevel;
        this.gear = gear;
    }
}
