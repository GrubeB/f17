package pl.app.unit.recruiter.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.common.mapper.BaseMapper;
import pl.app.unit.recruiter.application.domain.Recruiter;
import pl.app.unit.recruiter.query.dto.RecruiterDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
class RecruiterDtoQueryServiceImpl implements RecruiterDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public RecruiterDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<RecruiterDto> fetchByVillageId(@NonNull ObjectId villageId) {
        return repository.findById(villageId)
                .map(e -> mapper.map(e, RecruiterDto.class));
    }


    @Override
    public Flux<RecruiterDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, RecruiterDto.class));
    }


    interface Repository extends ReactiveMongoRepository<Recruiter, ObjectId> {
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(Recruiter.class, RecruiterDto.class, this::mapToRecruiterDto);
            addMapper(Recruiter.Request.class, RecruiterDto.RequestDto.class, this::mapToRecruiterDtoRequestDto);
        }
        RecruiterDto mapToRecruiterDto(Recruiter domain){
            return RecruiterDto.builder()
                    .villageId(domain.getVillageId())
                    .constructNumberMax(domain.getConstructNumberMax())
                    .requests(domain.getRequests().stream().map(e -> map(e, RecruiterDto.RequestDto.class)).collect(Collectors.toSet()))
                    .build();
        }
        RecruiterDto.RequestDto mapToRecruiterDtoRequestDto(Recruiter.Request domain) {
            return new RecruiterDto.RequestDto(
                    domain.getUnit(),
                    domain.getAmount(),
                    domain.getFrom(),
                    domain.getTo(),
                    domain.getCost()
            );
        }
    }
}
