package pl.app.config.kafka;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.app.comment.application.domain.CommentEvent;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class Producer {
    private final Logger logger = LoggerFactory.getLogger(Producer.class);
    private final KafkaTemplate<ObjectId, Object> template;

    @EventListener(ApplicationStartedEvent.class)
    public void generate() {
        Faker faker = Faker.instance();
        Flux<Long> interval = Flux.interval(Duration.ofMillis(1_000));
        Flux<String> quotes = Flux.fromStream(Stream.generate(() -> faker.hobbit().quote()));
        Flux.zip(interval, quotes).map((Function<Tuple2<Long, String>, Object>) objects -> {
//            logger.info("event published");
            return template.send("cs-domain-object-created", ObjectId.get(), new CommentEvent.DomainObjectCreatedEvent("topic", UUID.randomUUID().toString()));
        }).blockLast();
    }
}
