package pl.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import pl.app.common.shared.config.CustomArgumentResolver;
import pl.app.common.shared.config.ExceptionAdviceConfig;
import pl.app.common.shared.config.JacksonConfig;
import pl.app.common.shared.config.ModelMapperConfig;

@Configuration
@EnableWebFlux
@Import({
        JacksonConfig.class,
        ModelMapperConfig.class,
        ExceptionAdviceConfig.class
})
public class WebConfig implements WebFluxConfigurer {
    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        WebFluxConfigurer.super.configureArgumentResolvers(configurer);
        configurer.addCustomResolver(new CustomArgumentResolver.PageableHandlerMethodArgumentResolver());
    }

}

