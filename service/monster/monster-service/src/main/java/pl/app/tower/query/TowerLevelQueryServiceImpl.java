package pl.app.tower.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.monster_template.application.domain.MonsterTemplate;
import pl.app.monster_template.dto.MonsterTemplateDto;
import pl.app.common.mapper.BaseMapper;
import pl.app.monster_template.query.MonsterTemplateQueryService;
import pl.app.tower.application.domain.TowerLevel;
import pl.app.tower.dto.TowerLevelDto;
import pl.app.trader.application.domain.Trader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
class TowerLevelQueryServiceImpl implements TowerLevelQueryService {
    private final Mapper mapper;
    private final Repository repository;


    public TowerLevelQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }
    @Override
    public Mono<TowerLevelDto> fetchByLevel(@NonNull Integer id) {
        return repository.findByLevel(id)
                .map(e -> mapper.map(e, TowerLevelDto.class));
    }

    @Override
    public Mono<Page<TowerLevelDto>> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, TowerLevelDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), PageRequest.of(0, Integer.MAX_VALUE), tuple.getT2()));
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;
        @PostConstruct
        void init() {
            addMapper(TowerLevel.class, TowerLevelDto.class, e -> modelMapper.map(e, TowerLevelDto.class));
        }

    }

    interface Repository extends ReactiveMongoRepository<TowerLevel, ObjectId> {
        @Query("{ 'level': ?0 }")
        Mono<TowerLevel> findByLevel(Integer id);
    }
}
