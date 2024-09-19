package pl.app.god.application;

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
import pl.app.common.shared.model.Money;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.god.application.domain.God;
import pl.app.god.application.domain.GodEvent;
import pl.app.god.application.domain.GodException;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god.application.port.in.GodDomainRepository;
import pl.app.god.application.port.in.GodService;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GodMoneyServiceImplTest extends AbstractIntegrationTest {
    @Autowired
    private GodService godSerivce;
    @Autowired
    private GodMoneyServiceImpl godMoneyService;
    @SpyBean
    private GodDomainRepository godDomainRepository;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;

    @Test
    void addMoney_shouldAdd_whenGodExists() {
        final var godName = "TEST_NAME_" + UUID.randomUUID();
        final var accountId = ObjectId.get();
        final var amount = 100L;
        God god = godSerivce.create(new GodCommand.CreateGodCommand(accountId, ObjectId.get(), godName)).block();

        Mockito.reset(godDomainRepository, mongoTemplate, kafkaTemplate, topicNames);
        Assumptions.assumeThat(god).isNotNull();

        var command = new GodCommand.AddMoneyCommand(
                god.getId(),
                new Money(Money.Type.BASE, amount, Money.Type.PREMIUM, amount)
        );
        StepVerifier.create(godMoneyService.addMoney(command))
                .assertNext(next -> {
                    Assertions.assertThat(next.getMoney().getBalance(Money.Type.BASE)).isEqualTo(god.getMoney().getBalance(Money.Type.BASE) + amount);
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(God.class));
        verify(kafkaTemplate, times(2))
                .send(eq(topicNames.getMoneyAdded().getName()), eq(god.getId()), any(GodEvent.MoneyAddedEvent.class));
    }

    @Test
    void subtractMoney_shouldSubtract_whenGodExistsAndThereIsEnoughMoney() {
        final var godName = "TEST_NAME_" + UUID.randomUUID();
        final var accountId = ObjectId.get();
        final var amount = 100L;
        God god = godSerivce.create(new GodCommand.CreateGodCommand(accountId,  ObjectId.get(), godName)).block();
        godMoneyService.addMoney(new GodCommand.AddMoneyCommand(god.getId(), new Money(Money.Type.BASE, amount))).block();
        Assumptions.assumeThat(god).isNotNull();
        Mockito.reset(godDomainRepository, mongoTemplate, kafkaTemplate, topicNames);

        var command = new GodCommand.SubtractMoneyCommand(
                god.getId(),
                new Money(Money.Type.BASE, amount)
        );
        StepVerifier.create(godMoneyService.subtractMoney(command))
                .assertNext(next -> {
                    Assertions.assertThat(next.getMoney().getBalance(Money.Type.BASE)).isEqualTo(god.getMoney().getBalance(Money.Type.BASE));
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(God.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getMoneySubtracted().getName()), eq(god.getId()), any(GodEvent.MoneySubtractedEvent.class));
    }

    @Test
    void subtractMoney_shouldThrowException_whenThereIsNotEnoughMoney() {
        final var godName = "TEST_NAME_" + UUID.randomUUID();
        final var accountId = ObjectId.get();
        final var amount = 1_000_000L;
        God god = godSerivce.create(new GodCommand.CreateGodCommand(accountId, ObjectId.get(),  godName)).block();
        Assumptions.assumeThat(god).isNotNull();
        Mockito.reset(godDomainRepository, mongoTemplate, kafkaTemplate, topicNames);

        var command = new GodCommand.SubtractMoneyCommand(
                god.getId(),
                new Money(Money.Type.BASE, amount)
        );
        StepVerifier.create(godMoneyService.subtractMoney(command))
                .expectError(GodException.InsufficientMoneyException.class)
                .verify();
    }

    @Test
    void subtractMoney_shouldThrowException_whenAmountIsBelowZero() {
        final var godName = "TEST_NAME_" + UUID.randomUUID();
        final var accountId = ObjectId.get();
        final var amount = -1_000L;
        God god = godSerivce.create(new GodCommand.CreateGodCommand(accountId, ObjectId.get(), godName)).block();
        Assumptions.assumeThat(god).isNotNull();
        Mockito.reset(godDomainRepository, mongoTemplate, kafkaTemplate, topicNames);

        var command = new GodCommand.SubtractMoneyCommand(
                god.getId(),
                new Money(Money.Type.BASE, amount)
        );
        StepVerifier.create(godMoneyService.subtractMoney(command))
                .expectError(GodException.InvalidAmountException.class)
                .verify();
    }
}