package pl.app.character.application;

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
import pl.app.character.application.domain.*;
import pl.app.character.application.domain.Character;
import pl.app.character.application.port.in.CharacterCommand;
import pl.app.character.application.port.out.CharacterDomainRepository;
import pl.app.common.shared.model.StatisticType;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CharacterServiceImplTest extends AbstractIntegrationTest {
    @Autowired
    private CharacterServiceImpl service;
    @SpyBean
    private CharacterDomainRepository domainRepository;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;

    @Test
    void contextLoads() {
        assertThat(service).isNotNull();
        assertThat(domainRepository).isNotNull();
        assertThat(mongoTemplate).isNotNull();
        assertThat(kafkaTemplate).isNotNull();
        assertThat(topicNames).isNotNull();
    }

    @Test
    void createCharacter_shouldCreateCharacter_whenCommandIsValid() {
        final var characterName = "TEST_NAME" + UUID.randomUUID();
        final var characterProfession = CharacterProfession.WARRIOR;
        final var command = new CharacterCommand.CreateCharacterCommand(
                characterName,
                characterProfession
        );

        StepVerifier.create(service.createCharacter(command))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                    assertThat(next.getName()).isEqualTo(characterName);
                    assertThat(next.getProfession()).isEqualTo(characterProfession);
                }).verifyComplete();
        verify(mongoTemplate, times(1)).insert(any(Character.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getCharacterCreated().getName()), any(), any(CharacterEvent.CharacterCreatedEvent.class));
    }

    @Test
    void createCharacter_shouldThrowException_whenNameIsDuplicated() {
        final var characterName = "TEST_NAME_" + UUID.randomUUID();
        final var characterProfession = CharacterProfession.WARRIOR;
        final var command = new CharacterCommand.CreateCharacterCommand(
                characterName,
                characterProfession
        );
        service.createCharacter(command).block();
        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);

        StepVerifier.create(service.createCharacter(command))
                .expectError(CharacterException.DuplicatedNameException.class)
                .verify();
    }

    @Test
    void addStatistic_shouldAddStatistic_whenCommandIsValidAndCharacterExists() {
        final var characterName = "TEST_NAME_" + UUID.randomUUID();
        final var characterProfession = CharacterProfession.WARRIOR;
        final var statisticName = StatisticType.PERSISTENCE.name();
        final var statisticQuantity = 1L;
        Character character = service.createCharacter(new CharacterCommand.CreateCharacterCommand(characterName, characterProfession)).block();

        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);
        Assumptions.assumeThat(character).isNotNull();

        final var command = new CharacterCommand.AddStatisticCommand(
                character.getId(),
                statisticName,
                statisticQuantity
        );

        StepVerifier.create(service.addStatistic(command))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                    assertThat(next.getStatistics().getPersistence()).isEqualTo(character.getStatistics().getPersistence() + statisticQuantity);
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Character.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getStatisticAdded().getName()), eq(character.getId()), any(CharacterEvent.StatisticAddedEvent.class));
    }
}