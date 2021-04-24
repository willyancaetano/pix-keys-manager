package com.github.willyancaetano.pix.keys.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.github.willyancaetano.pix.keys.manager.controller"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
            .title("Pix keys Manager API")
            .description("\"A simple API for manage pix keys\"")
            .version("1.0.0")
            .license("MIT")
            .licenseUrl("https://opensource.org/licenses/MIT\"")
            .build();
    }
}