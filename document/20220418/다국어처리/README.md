```
@date   : 2022-04-19 00:22
@author : ymkim
@desc   : Internationalization for RESTful Services(다국어 처리) 
```

## 01. Internationalization(다국어 처리)

특정한 Controller에서만 적용이 되는 것이 아니라, 글로벌(전역)으로 적용이 되어야 한다.

### 01-1. @Configuration 등록

- LocaleResolver
- Default Locale
  - Locale.US or Locale.KOREA
- ResourceBundleMessageSource

### 01-2. Usage

- generate message bundle files
- @Autowired MessageSource
- @RequestHeader(value = "Accept-Language", required = false) Local local
- messageSource.getMessage("greeting.message", null, local)

### 01-3. Source

**RestfulWebServiceApplication**

```java
@SpringBootApplication
public class RestfulWebServiceApplication {

    public static void main(String[] args) {
      SpringApplication.run(RestfulWebServiceApplication.class, args);
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaledResolver localeResolver = new SessionLocaledResolver();
        localeResolver.setDefaultLocale(Locale.KOREA);
        return localeResolver;
    }
}
```

- Spring Boot Application 로드(load) 시점에 LocaleResolver Bean을 초기화.
- 단일 컨트롤러가 아닌, 다수의 컨트롤러에서 다국어 처리를 하기 위한 목적으로 사용.

**application.yml**

```yaml
spring:
  messages:
    basename: messages
```

- yaml 파일에 다국어 관련 Config 설정.

**messages.xx.properties**

```properties
# ko
greeting.messages=안녕하세요
```

```properties
# en
greeting.message=Hello
```

```properties
# fr
greeting.message=Bonjour
```

**InternationalController**

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class InternationalController {
    private final Logger log = LoggerFactory.getLogger(InternationalController.class);
    private final MessageSource messageSource;

   /**
    * 다국어 처리를 위한 API
    *
    * @param acceptLanguage
    * @return
    */
    @GetMapping("/language")
    public String handleInternationalLanguage(@RequestHeader(name = "Accept-Language", required = false) Locale acceptLanguage) {
        log.info("acceptLanguage = {}", acceptLanguage);
        return messageSource.getMessage("greeting.message", null, acceptLanguage);
    }
}
```

- 다국어 처리를 위한 API를 위와 같이 생성한다.
- import org.springframework.context.MessageSource
  - MessageSource 객체를 import 해서 사용. 