package com.restful.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(Arrays.asList("application/json", "application/xml"));

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any()) // Swagger API 문서로 만들기 원하는 BasePackage 설정
                .paths(PathSelectors.any()) // URL 경로 지정, 특정 URL에 해당하는 요청만 Swagger API Document 생성
                .build()
                .apiInfo(apiInfo())
                .enable(true)
                .produces(getProduceContentsType())
                .consumes(getConsumeContentsTypes());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("REST API TEST")
                .description("REST API TEST를 위한 Swagger - Document")
                .contact(new Contact("YoungMin Kim", "https://github.com/ym1085", "youngmin1085@gmail.com"))
                .version("1.0")
                .build();
    }

    private Set<String> getProduceContentsType() {
        return new HashSet<>(Arrays.asList(
                "application/json;charset=UTF-8",
                "application/x-www-form-urlencoded"
        ));
    }

    private Set<String> getConsumeContentsTypes() {
        return new HashSet<>(Arrays.asList(
                "application/json;charset=UTF-8"
        ));
    }
}
