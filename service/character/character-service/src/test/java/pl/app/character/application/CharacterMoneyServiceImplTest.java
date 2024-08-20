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
import pl.app.AbstractIntegrationTest;
import pl.app.character.application.domain.Character;
import pl.app.character.application.domain.CharacterEvent;
import pl.app.character.application.domain.CharacterException;
import pl.app.character.application.domain.CharacterProfession;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.application.port.in.CharacterService;
import pl.app.character.application.port.out.CharacterDomainRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CharacterMoneyServiceImplTest extends AbstractIntegrationTest {
    @Autowired
    private CharacterService characterService;
    @Autowired
    private CharacterMoneyServiceImpl characterMoneyService;
    @SpyBean
    private CharacterDomainRepository domainRepository;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;

    @Test
    void addMoney_shouldAdd_whenCharacterExists() {
        final var characterName = "TEST_NAME_" + UUID.randomUUID();
        final var characterProfession = CharacterProfession.WARRIOR;
        final var amount = 100L;
        Character character = characterService.createCharacter(new CharacterCommand.CreateCharacterCommand(characterName, characterProfession)).block();

        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);
        Assumptions.assumeThat(character).isNotNull();

        var command = new CharacterCommand.AddMoneyCommand(
                character.getId(),
                amount
        );
        StepVerifier.create(characterMoneyService.addMoney(command))
                .assertNext(next -> {
                    Assertions.assertThat(next.getMoney().getBalance()).isEqualTo(character.getMoney().getBalance() + amount);
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Character.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getMoneyAdded().getName()), eq(character.getId()), any(CharacterEvent.MoneyAddedEvent.class));
    }

    @Test
    void subtractMoney_shouldSubtract_whenCharacterExistsAndThereIsEnoughMoney() {
        final var characterName = "TEST_NAME_" + UUID.randomUUID();
        final var characterProfession = CharacterProfession.WARRIOR;
        final var amount = 100L;
        Character character = characterService.createCharacter(new CharacterCommand.CreateCharacterCommand(characterName, characterProfession)).block();
        characterMoneyService.addMoney(new CharacterCommand.AddMoneyCommand(character.getId(), amount * 2)).block();
        Assumptions.assumeThat(character).isNotNull();
        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);

        var command = new CharacterCommand.SubtractMoneyCommand(
                character.getId(),
                amount
        );
        StepVerifier.create(characterMoneyService.subtractMoney(command))
                .assertNext(next -> {
                    Assertions.assertThat(next.getMoney().getBalance()).isEqualTo(character.getMoney().getBalance() + amount);
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Character.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getMoneySubtracted().getName()), eq(character.getId()), any(CharacterEvent.MoneySubtractedEvent.class));
    }

    @Test
    void subtractMoney_shouldThrowException_whenThereIsNotEnoughMoney() {
        final var characterName = "TEST_NAME_" + UUID.randomUUID();
        final var characterProfession = CharacterProfession.WARRIOR;
        final var amount = 1_000_000L;
        Character character = characterService.createCharacter(new CharacterCommand.CreateCharacterCommand(characterName, characterProfession)).block();
        Assumptions.assumeThat(character).isNotNull();
        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);

        var command = new CharacterCommand.SubtractMoneyCommand(
                character.getId(),
                amount
        );
        StepVerifier.create(characterMoneyService.subtractMoney(command))
                .expectError(CharacterException.InsufficientMoneyException.class)
                .verify();
    }

    @Test
    void subtractMoney_shouldThrowException_whenAmountIsBelowZero() {
        final var characterName = "TEST_NAME_" + UUID.randomUUID();
        final var characterProfession = CharacterProfession.WARRIOR;
        final var amount = -1_000L;
        Character character = characterService.createCharacter(new CharacterCommand.CreateCharacterCommand(characterName, characterProfession)).block();
        Assumptions.assumeThat(character).isNotNull();
        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);

        var command = new CharacterCommand.SubtractMoneyCommand(
                character.getId(),
                amount
        );
        StepVerifier.create(characterMoneyService.subtractMoney(command))
                .expectError(CharacterException.InvalidAmountException.class)
                .verify();
    }
}