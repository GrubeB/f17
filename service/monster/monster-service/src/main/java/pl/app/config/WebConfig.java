package pl.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import pl.app.common.shared.config.CustomArgumentResolver;
import pl.app.common.shared.config.ExceptionAdviceConfig;
import pl.app.common.shared.config.JacksonConfig;
import pl.app.common.shared.config.ModelMapperConfig;

import java.util.Arrays;

@Configuration
@EnableWebFlux
@Import({
        JacksonConfig.class,
        ModelMapperConfig.class,
        ExceptionAdviceConfig.class
})
@RequiredArgsConstructor
public class WebConfig implements WebFluxConfigurer {


    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        WebFluxConfigurer.super.configureArgumentResolvers(configurer);
        configurer.addCustomResolver(new CustomArgumentResolver.PageableHandlerMethodArgumentResolver());
    }

    private final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        ObjectMapper objectMapper = jackson2ObjectMapperBuilder.build();
        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}

