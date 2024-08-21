package pl.app.battle.application;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import pl.app.battle.application.domain.BattleEvent;
import pl.app.battle.application.domain.BattleResult;
import pl.app.battle.application.port.in.BattleCommand;
import pl.app.battle.application.port.out.CharacterRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BattleServiceImplTest {
    @Autowired
    private BattleServiceImpl service;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;
    @SpyBean
    private CharacterRepository characterRepository;

    @Test
    void startDuelBattle() {
        final var player1Id = new ObjectId("66c53e13137705259bc4826b");
        final var player2Id = new ObjectId("66c53ec7137705259bc4826c");

        final var command = new BattleCommand.StartDuelBattleCommand(
                player1Id,
                player2Id
        );
        StepVerifier.create(service.startDuelBattle(command))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).insert(any(BattleResult.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getBattleEnded().getName()), any(), any(BattleEvent.BattleEndedEvent.class));

    }
}