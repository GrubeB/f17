package pl.app.unit.recruiter.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.building.builder.application.port.in.BuilderCommand;
import pl.app.building.builder.application.port.in.BuilderService;
import pl.app.building.builder.query.BuilderDtoQueryService;
import pl.app.building.builder.query.dto.BuilderDto;
import pl.app.unit.recruiter.application.port.in.RecruiterCommand;
import pl.app.unit.recruiter.application.port.in.RecruiterService;
import pl.app.unit.recruiter.query.RecruiterDtoQueryService;
import pl.app.unit.recruiter.query.dto.RecruiterDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(RecruiterRestController.resourcePath)
@RequiredArgsConstructor
class RecruiterRestController {
    public static final String resourceName = "recruiters";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final RecruiterService service;
    private final RecruiterDtoQueryService queryService;


    @PostMapping("/{villageId}/requests")
    Mono<ResponseEntity<RecruiterDto>> add(@RequestBody RecruiterCommand.AddRecruitRequestCommand command) {
        return service.add(command)
                .flatMap(domain -> queryService.fetchByVillageId(domain.getVillageId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{villageId}/requests")
    Mono<ResponseEntity<RecruiterDto>> add(@RequestBody RecruiterCommand.RemoveRecruitRequestCommand command) {
        return service.remove(command)
                .flatMap(domain -> queryService.fetchByVillageId(domain.getVillageId()))
                .map(ResponseEntity::ok);
    }
}
