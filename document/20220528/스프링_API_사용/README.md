## Spring Boot API 사용

## 01. HATEOAS?

HATEOAS란 Hypermedia As the Engine Of Application State의 약자로써 현재 리소스와 연관된(호출 가능한) 자원 상태 정보를 제공
하는 것을 의미한다. 쉽게 말해 서버가 클라이언트에게 `하이퍼 미디어`를 통해 정보를 동적으로 제공해 주는 것이다.

- API에서 해당 리소스에 대해 어떠한 행동(Action)을 할 수 있는지 URL을 전달하여 클라이언트가 참고하고 사용한다
- 해당 리소스의 상태에 따라 링크 정보가 바뀌며 동적으로 리소스를 구성 한다

### 01-1. build.gradle

```groovy
implementation 'org.springframework.boot:spring-boot-starter-hateoas' // spring-boot-starter-hateoas 추가
```

### 01-2. 버전별 설정

`spring 2.1.8.RELEASE 일 경우`

```java
@GetMapping("/users/{id}")
public Resource<User> retrieveUser(@PathVariable("id") int id) {
    //...
    Resource<User> resource = new Resource<>(user);
    ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUser());
    resource.add(linkTo.withRel("all-users"));
    
    return resource;
}
```

`spring 2.2 이상일 경우`

```java
@GetMapping("/users/{id}")
public Resource<User> retrieveUser(@PathVariable("id") int id) {
    //...
    EntityModel<User> model = new EntityModel<>(user);
    WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUser());
    model.add(linkTo.withRel("all-users"));
    
    return model;
}
```

- 실제 구현은 2.2 이상의 소스를 적용하였다

### 01-3. 결과

```
http://localhost:8088/user/1
```

```json
{
    "id": 1,
    "userName": "Kenneth",
    "joinDate": "2022-05-28T09:21:52.141+00:00",
    "password": "pass1",
    "ssn": "930823-1065627",
    "_links": {
        "all-users": {
            "href": "http://localhost:8088/user"
        }
    }
}
```

- /user/{id} 에 요청을 날린 결과는 위와 같다
- "_links" 안에 클라이언트가 해당 요청에 대해 참고할 수 있는 리소스 존재

## 02. Swagger API

- REST 웹 서비스를 제공하려면 프론트앤드 개발자에게 전달해줄 API 문서가 필수적
- Excel이나, Word와 같은 형식으로 API 문서를 만드는 방법도 있지만, 시간에 있어 비효율 적이다
- 위 같은 이유로 인해 나온 것이 Swagger이며, Swagger는 `문서`뿐만 아니라, `빌드`, `테스트 케이스`도 작성이 가능하다

## 02-1. build.gradle

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'

    implementation group: 'io.springfox', name: 'springfox-boot-starter', version: '3.0.0'
    implementation group: 'io.springfox', name: 'springfox-swagger-ui', version: '3.0.0'
}
```

- 위와 같이 swagger 관련 설정을 추가 한다 

## 02-2. Swagger Config 설정

```java
// AS-IS
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private static final Contact DEFAULT_CONTACT =
            new Contact("YoungMin Kim", "https://github.com/ym1085", "youngmin1085@gmail.com");
  
    private static final ApiInfo DEFAULT_API_INFO =
            new ApiInfo("RESTful API TEST", "My User management API Service", "1.0", "urn:tos",
                    DEFAULT_CONTACT, "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", new ArrayList<>());
  
    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(
            Arrays.asList("application/json", "application/xml"));
  
    @Bean
    public Docket api() {
      return new Docket(DocumentationType.SWAGGER_2)
              .apiInfo(DEFAULT_API_INFO)
              .produces(DEFAULT_PRODUCES_AND_CONSUMES)
              .consumes(DEFAULT_PRODUCES_AND_CONSUMES);
    }
}
```

- URL 확인 방법은 아래와 같다 (Swagger 3.0 기준)
  - http://localhost:8088/v2/api-docs
  - http://localhost:8088/swagger-ui/index.html

```java
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
```

- 위 내용은 기존 Swagger API를 필자의 마음대로 커스텀 해본 소스
- Swagger option에 대한 내용만 추가적으로 정리하면 될 듯

### 참고 자료

- [Level3 단계의 REST API 구현을 위한 HATEOAS 적용](https://www.inflearn.com/course/spring-boot-restful-web-services/lecture/39110?volume=1.00&mm=null&tab=note)
- [REST API Documentations을 위한 Swagger 사용](https://www.inflearn.com/course/spring-boot-restful-web-services/lecture/39111?volume=1.00&mm=null&tab=note)
- [Spring HATEOAS 사용해보기](https://brunch.co.kr/@purpledev/29)
- [Swagger 간단히 구현](https://milenote.tistory.com/67)