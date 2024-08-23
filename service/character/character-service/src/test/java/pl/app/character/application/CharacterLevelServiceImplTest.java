package pl.app.character.application;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import pl.app.character.application.domain.Character;
import pl.app.character.application.domain.CharacterEvent;
import pl.app.character.application.domain.CharacterProfession;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.application.port.in.CharacterService;
import pl.app.character.application.port.out.CharacterDomainRepository;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CharacterLevelServiceImplTest extends AbstractIntegrationTest {
    @Autowired
    private CharacterService characterService;
    @Autowired
    private CharacterLevelServiceImpl characterLevelService;
    @SpyBean
    private CharacterDomainRepository domainRepository;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;


    @Test
    void addExp_shouldAdd_whenCharacterExists() {
        final var characterName = "TEST_NAME_" + UUID.randomUUID();
        final var characterProfession = CharacterProfession.WARRIOR;
        final var exp = 10_000L;
        Character character = characterService.createCharacter(new CharacterCommand.CreateCharacterCommand(characterName, characterProfession)).block();

        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);
        Assumptions.assumeThat(character).isNotNull();

        var command = new CharacterCommand.AddExpCommand(
                character.getId(),
                exp
        );
        StepVerifier.create(characterLevelService.addExp(command))
                .assertNext(next -> {
                    assertThat(next.getLevel().getExp()).isEqualTo(exp);
                    assertThat(next.getLevel().getLevel()).isGreaterThan(1);
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Character.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getExpAdded().getName()), eq(character.getId()), any(CharacterEvent.ExpAddedEvent.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getCharacterLevelIncreased().getName()), eq(character.getId()), any(CharacterEvent.CharacterLevelIncreasedEvent.class));
    }
}