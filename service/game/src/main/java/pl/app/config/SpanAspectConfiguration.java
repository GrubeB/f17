package pl.app.config;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpanAspectConfiguration {
    @Bean
    NewSpanParser newSpanParser() {
        return new DefaultNewSpanParser();
    }

    @Bean
    MethodInvocationProcessor methodInvocationProcessor(NewSpanParser newSpanParser, Tracer tracer,
                                                        BeanFactory beanFactory) {
        return new ImperativeMethodInvocationProcessor(newSpanParser, tracer, beanFactory::getBean,
                beanFactory::getBean);
    }

    @Bean
    SpanAspect spanAspect(MethodInvocationProcessor methodInvocationProcessor) {
        return new SpanAspect(methodInvocationProcessor);
    }
}
