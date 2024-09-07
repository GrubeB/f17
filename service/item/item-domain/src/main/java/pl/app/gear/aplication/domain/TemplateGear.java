package pl.app.gear.aplication.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class TemplateGear {
    private ObjectId helmetTemplateId;
    private ObjectId armorTemplateId;
    private ObjectId glovesTemplateId;
    private ObjectId bootsTemplateId;
    private ObjectId beltTemplateId;
    private ObjectId ringTemplateId;
    private ObjectId amuletTemplateId;
    private ObjectId talismanTemplateId;

    private ObjectId leftHandTemplateId;
    private ObjectId rightHandTemplateId;

    public TemplateGear() {
    }

    public TemplateGear(ObjectId helmetTemplateId, ObjectId armorTemplateId, ObjectId glovesTemplateId, ObjectId bootsTemplateId, ObjectId beltTemplateId, ObjectId ringTemplateId, ObjectId amuletTemplateId, ObjectId talismanTemplateId, ObjectId leftHandTemplateId, ObjectId rightHandTemplateId) {
        this.helmetTemplateId = helmetTemplateId;
        this.armorTemplateId = armorTemplateId;
        this.glovesTemplateId = glovesTemplateId;
        this.bootsTemplateId = bootsTemplateId;
        this.beltTemplateId = beltTemplateId;
        this.ringTemplateId = ringTemplateId;
        this.amuletTemplateId = amuletTemplateId;
        this.talismanTemplateId = talismanTemplateId;
        this.leftHandTemplateId = leftHandTemplateId;
        this.rightHandTemplateId = rightHandTemplateId;
    }
}
