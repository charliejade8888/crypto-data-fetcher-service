package com.tyrell.replicant.crypto.data.fetcher.service.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Profile(value = {"swagger"})
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    //pass the following to VM options to activate
    // -Dspring.profiles.active=swagger

    //FIXME!! not working so debug
    @Bean
    public Docket api() {
        String restAPITitle = "Crypto Data Fetcher Service";
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error"))) // Exclude Spring error controllers
                .build()
                .apiInfo(new ApiInfo(restAPITitle, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY));
    }

}