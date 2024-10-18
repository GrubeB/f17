package pl.app.resource.village_resource.application.domain;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.app.resource.resource.application.domain.Resource;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class VillageResourceTest {

    @Test
    void refreshResource() {
        var villageId = ObjectId.get();
        var initialTime = Instant.parse("2024-01-01T10:00:00.00Z");
        var after10Minutes = initialTime.plus(Duration.ofMinutes(10));

        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class)) {
            mockedInstant.when(Instant::now).thenReturn(initialTime);

            VillageResource villageResource = new VillageResource(villageId,Resource.zero());

            mockedInstant.when(Instant::now).thenReturn(after10Minutes);

            Resource added = villageResource.refreshResource(new Resource(3_600,3_600,3_600,0), new Resource(3_600,3_600,3_600,0));

            assertEquals(360, added.getWood());
            assertEquals(360, added.getClay());
            assertEquals(360, added.getIron());
        }
    }
    @Test
    void refreshResource2() throws InterruptedException {
        var villageId = ObjectId.get();
        VillageResource villageResource = new VillageResource(villageId,Resource.zero());
        Thread.sleep(2_000);
        Resource added = villageResource.refreshResource(new Resource(3_600,3_600,3_600,0), new Resource(3_600,3_600,3_600,0));

        assertEquals(2, added.getWood());
        assertEquals(2, added.getClay());
        assertEquals(2, added.getIron());

    }
}