package pl.app.gold_coin.gold_coin.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.gold_coin.gold_coin.application.domain.PlayerGoldCoin;
import pl.app.gold_coin.gold_coin.application.domain.PlayerGoldCoinEvent;
import pl.app.gold_coin.gold_coin.application.domain.PlayerGoldCoinException;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.village.village.query.VillageDtoQueryService;
import pl.app.village.village.query.dto.VillageDto;
import reactor.core.publisher.Mono;

import java.util.function.Function;


@Service
@RequiredArgsConstructor
class PlayerGoldCoinServiceImpl implements PlayerGoldCoinService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerGoldCoinServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final PlayerGoldCoinDomainRepository playerGoldCoinDomainRepository;

    private final VillageDtoQueryService villageDtoQueryService;
    private final VillageResourceService villageResourceService;

    @Override
    public Mono<PlayerGoldCoin> crate(PlayerGoldCoinCommand.CreatePlayerGoldCoinCommand command) {
        return Mono.fromCallable(() ->
                mongoTemplate.exists(Query.query(Criteria.where("playerId").is(command.getPlayerId().toHexString())), PlayerGoldCoin.class)
                        .flatMap(exist -> exist ? Mono.error(PlayerGoldCoinException.DuplicatedPlayerIdException.fromId(command.getPlayerId().toHexString())) : Mono.empty())
                        .then(Mono.defer(() -> {
                            var domain = new PlayerGoldCoin(command.getPlayerId());
                            var event = new PlayerGoldCoinEvent.PlayerGoldCoinCreatedEvent(domain.getPlayerId());
                            return mongoTemplate.insert(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getPlayerGoldCoinCreated().getName(), domain.getPlayerId(), event)))
                                    .thenReturn(domain);
                        }))
        ).doOnSubscribe(subscription ->
                logger.debug("crating gold coin for player: {}", command.getPlayerId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created gold coin for player: {}", domain.getPlayerId())
        ).doOnError(e ->
                logger.error("exception occurred while crating gold coin for player: {}, exception: {}", command.getPlayerId(), e.toString())
        );
    }

    @Override
    public Mono<PlayerGoldCoin> mint(PlayerGoldCoinCommand.MintGoldCoinCommand command) {
        return Mono.fromCallable(() ->
                villageDtoQueryService.fetchById(command.getVillageId())
                        .flatMap(village -> verifyVillageMeetRequirements(village)
                                .then(subtractResources(village, command.getAmount()))
                                .then(playerGoldCoinDomainRepository.fetchByPlayerId(village.getOwnerId()))
                        )
                        .flatMap(domain -> {
                            domain.add(command.getAmount());
                            var event = new PlayerGoldCoinEvent.MoneyAddedEvent(domain.getPlayerId(), command.getAmount());
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getGoldCoinAdded().getName(), domain.getPlayerId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("minting gold coin from village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("minted gold coin to player: {}", domain.getPlayerId())
        ).doOnError(e ->
                logger.error("exception occurred while minting gold coin from village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    private Mono<Void> subtractResources(VillageDto village, Integer amount) {
        var command = new VillageResourceCommand.SubtractResourceCommand(village.getId(), PlayerGoldCoin.GOLD_COIN_COST.multiply(amount));
        return villageResourceService.subtract(command)
                .then();
    }

    private Mono<Void> verifyVillageMeetRequirements(VillageDto village) {
        if (!village.getVillageInfrastructure().getBuildings().meetRequirements(BuildingType.ACADEMY, 1)) {
            return Mono.error(new PlayerGoldCoinException.NotFoundPlayerGoldCoinException());
        }
        return Mono.empty();
    }
}
