package pl.app.resource.village_resource.application.domain;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import pl.app.resource.share.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VillageResourceTest {

    @Test
    void refreshResource2() throws InterruptedException {
        var villageId = ObjectId.get();
        VillageResource villageResource = new VillageResource(villageId, Resource.zero());
        Thread.sleep(2_000);
        Resource added = villageResource.refreshResource(new Resource(3_600, 3_600, 3_600, 0), new Resource(3_600, 3_600, 3_600, 0));

        assertEquals(2, added.getWood());
        assertEquals(2, added.getClay());
        assertEquals(2, added.getIron());

    }
}