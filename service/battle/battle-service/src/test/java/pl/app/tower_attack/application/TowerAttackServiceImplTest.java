package pl.app.tower_attack.application;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.tower_attack.application.port.in.TowerAttackCommand;
import pl.app.unit.application.port.in.CharacterRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TowerAttackServiceImplTest {
    @Autowired
    private TowerAttackServiceImpl service;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;
    @SpyBean
    private CharacterRepository characterRepository;


    @Test
    void test() throws InterruptedException {
        final var god1 = new ObjectId("66e4d099a0ae81540a6e2952");
        final var character1 = new ObjectId("66e4d0c5241d431adb974594");
        final var command = new TowerAttackCommand.AttackTowerCommand(
                god1,
                Set.of(character1),
                1
        );
        service.attackTower(command).subscribe();

        Thread.sleep(30_000);
    }
}