package pl.app.inventory.inventory.query;

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
import pl.app.inventory.inventory.application.domain.Inventory;
import pl.app.inventory.inventory.query.dto.InventoryDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class InventoryQueryServiceImpl implements InventoryQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public InventoryQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<InventoryDto> fetchByPlayerId(@NonNull ObjectId playerId) {
        return repository.findById(playerId)
                .map(e -> mapper.map(e, InventoryDto.class));
    }

    @Override
    public Flux<InventoryDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, InventoryDto.class));
    }

    interface Repository extends ReactiveMongoRepository<Inventory, ObjectId> {
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(Inventory.class, InventoryDto.class, e -> modelMapper.map(e, InventoryDto.class));
        }

    }
}
