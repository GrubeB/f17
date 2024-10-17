package pl.app.resource.village_resource.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.resource.resource.application.domain.Resource;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VillageResourceDto implements Serializable {
    private ObjectId villageId;
    private Resource resource;
}