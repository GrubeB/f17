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

import java.util.Set;

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
        final var character1 = new ObjectId("66c96eaa861eff4b1712b842");
        final var character2 = new ObjectId("66c98909ef56735cc9423219");
        final var god1 = new ObjectId("66c96df72d4c6a4a17f9fdb8");

        final var command = new BattleCommand.StartDuelBattleCommand(
                god1,
                character1,
                god1,
                character2
        );
        StepVerifier.create(service.startDuelBattle(command))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).insert(any(BattleResult.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getBattleEnded().getName()), any(), any(BattleEvent.BattleEndedEvent.class));
    }

    @Test
    void startTwoGodBattle(){
        final var god1 = new ObjectId("66c92143e07dfa3b20f708b8");
        final var character1 = new ObjectId("66ca1e524bfa5928c00bcffa");
        final var god2 = new ObjectId("66c96df72d4c6a4a17f9fdb8");
        final var character2 = new ObjectId("66c96eaa861eff4b1712b842");

        final var command = new BattleCommand.StartTwoGodBattleCommand(
                god1,
                Set.of(character1),
                god2,
                Set.of(character2)
        );
        StepVerifier.create(service.startTwoGodBattle(command))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).insert(any(BattleResult.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getBattleEnded().getName()), any(), any(BattleEvent.BattleEndedEvent.class));
    }
}