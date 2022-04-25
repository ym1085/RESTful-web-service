```
@date   : 2022-04-10 13:45
@author : ymkim
@desc   : HTTP Status Code 제어와 Exception Handling 
```

## 01. User Service API 구현

### 01-1. ServletUriComponentsBuilder

- UriComponentsBuilder 클래스를 상속.
- `이전 요청의 URI`를 `재사용`하여 보다 `편리하게 URI를 사용하기 위한 목적`.
- UriComponentsBuilder의 `Static Factory Method`와 더불어, `추가적인 메서드` 제공.

```java
@RestController
@RequiredArgsConstructor
public class UserController {
    @PostMapping("/user")
    public ResponseEntity<User> save(@RequestBody User user) {
        log.debug("user = {}", user.toString());
        User savedUser = userService.save(user);

        // fromCurrentRequest() : 현재 요청되어진 request값을 사용한다는 의미
        // path() : 반환 시켜줄 값
        // buildAndExpand() : Build와 Expand를 한 번에 작성
        // savedUser.getId() : /{id} 가변변수에 새롭게 만들어진 id값 저장
        // toUri() : URI 형태로 변환
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(savedUser.getId())
                                                  .toUri();

        return ResponseEntity.created(location).build();
    }    
}
```

- ServletUriComponentsBuilder 간단히 짚고 넘어가기.

### 01-2. HTTP Status Code Exception Handling

```java
// AS-IS
@RestController
@RequiredArgsConstructor
public class UserController {
    
    //단일 회원 조회
    @GetMapping("/user/{id}")
    public User findById(@PathVariable int id) {
        return userService.findById(id);
    }
}
```

현재 기존 단일 회원 조회 API의 경우 `존재하지 않는 회원의 ID` 를 입력하여도 `Status code 200` 을 반환하는 상황이다.  
이러한 결과를 `예외 처리 클래스`를 생성하여 처리 해보자.

```java
// TO-BE
@RestController
@RequiredArgsConstructor
public class UserController {

    //단일 회원 조회
    @GetMapping("/user/{id}")
    public User findById(@PathVariable int id) {
        User user = userService.save(user);
        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }
        return user;
    }
}
```

```java
// AS-IS : 정확한 예외를 지정하기 전
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}

```

- 위와 같이 간단한 예외 클래스(UserNotFoundException)를 생성하여 해당 예외를 처리한다.
- 다음은 어노테이션을 사용하여 예외 처리를 원하는 `HttpStatus`를 지정 해보자. 

```java
// HTTP Status code
// 2XX -> OK
// 4XX -> Client Error [Auth..]
// 5XX -> Server Error [Server program..]

// TO-BE : 어노테이션을 사용하여 예외 지정
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
```

- @ResponseStatus(HttpStatus.NOT_FOUND)를 사용하여 요청에 맞는 응답 예외를 반환.
- 서버 측 Error가 아니라, 사용자가 요청한 Resource 가 존재하지 않기에 404 Error 반환.

### 01-3. Exception Handling with AOP

이번에는 글로벌 예외처리를 작성하기 위한 프로세스에 대해 간략히 알아보자.

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private Date timestamp;
    private String message;
    private String details;
}
```

- 공통 예외 처리 code를 반환할 Response 클래스 생성.

```java
@RestController
@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {
    // ...
}
```

- `글로벌 예외 처리 클래스`.
- 예외 처리를 수행할 Handler 예외 클래스 생성.
- @ControllerAdvice는 모든 컨트롤러가 실행될 때 반드시 Advice 컨트롤러를 가진 bean이 사전에 실행 된다. 

```java
@RestController
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 공통 예외 처리 클래스
    @ExceptionHandler(Exception.class)
    // @ExceptionHandler({Exception.class, RunTimeException.class}) -> 이런식으로 중복도 가능할거임
    public final ResponseEntity<Object> handleException(Exception e, WebRequest request) {
        // 위에서 생성한 ExceptionResponse 객체 생성 후 값 셋팅
        ExceptionResponse response = new ExceptionResponse(new Date(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }    
}
```

- 글로벌 예외 처리 클래스를 생성하여, 공통 예외 처리를 수행 한다.
- @ExceptionHandler 어노테이션에 처리할 예외를 기재 한다. 
- 다음은 존재하지 않는 유저를 조회 했을 경우 처리할 예외 처리 메서드를 추가 해보자.

```java
@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
    
    //.. 중략
    
    /* UserNotFound Exception을 처리하기 위한 메서드 작성 */
    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(Exception e, WebRequest request) {
        ExceptionResponse response =
                new ExceptionResponse(new Date(), e.getMessage(), request.getDescription(false));

        return new ResponseEntity(response, HttpStatus.NOT_FOUND);
    }    
}
```

- 위와 같이 UserNotFoundException을 처리하기 위한 메서드를 작성한다.
- 만약 유저가 존재하지 않는 요청(Request)를 하는 경우 결과는 다음과 같다.

```json
{
    "timestamp": "2022-04-10T06:39:41.494+00:00",
    "message": "ID[100] not found",
    "details": "uri=/user/100"
}
```

- Response 값에 Trace log를 제외하고 보여줄 수 있음.

## 02. 무분별한 커스텀 예외 정의의 단점

> 참고 내용은 하단에 기재 해두었습니다.

강의를 수강하는 중에 `Global 커스텀 예외를 정의하는 것이 과연 좋은가?` 에 대한 생각이 들어 글을 남겨둔다.  

### 02-1. 표준 예외를 사용하는것이 오히려 가독성에 좋을수도 있다?

> "자동차 이름의 글자 수가 유효하지 않은 경우를 표시하는 예외를 정의"

- `InvalidCarNameLengthCustomException` 커스텀 예외 클래스 작성.
- 해당 예외를 작성한 개발자는 확실하게 해당 예외를 파악하고 있지만, 다른 개발자들은 어떨까?
- 해당 예외의 이름을 알아볼 수 있지만, `InvalidKnowledgeCustomException`와 같은 이름이면 파악이 힘들 것이다.
- 즉, 커스텀 예외도 나쁘지는 않지만 `표준` 예외가 편한 상황이 존재할 수 있다는 의미다.
  - IllegalArgumentException
  - IllegalStateException
  - NullPointerException

### 02-2. 비용 문제

- 커스텀 예외 클래스가 너무 많으면 메모리 사용량이 늘고, 클래스 로딩 시간도 느려진다.

### 02-3. 표준 예외

커스텀 예외 클래스가 나쁘다는 것은 아니다, 웬만한 부분은 표준 예외로 처리하고  
`구체적인 처리가 필요한 경우`에는 커스텀 예외를 작성해 처리하자.

| 표준 예외 처리명                         | 내용                              |
|-----------------------------------|---------------------------------|
| **IllegalArgumentException**      | 인자 값이 잘못 되었을 때                  |
| **IllegalStateException**         | 메소드가 요구된 처리를 하기에 적합한 상태에 있지 않을때 |
| **NullPointerException**          | null 허용을 안하는 메서드에 null이 나왔을 때   |
| **IndexOutOfBoundsException**     | 인덱스 범위를 넘어섰을 때                  |
| **ConcurrentModification**        | 허용하지 않는 동시 수정이 발견 되었을 때         |
| **UnSupportedOperationException** | 호출한 메서드를 지원하지 않을 때              |

## 참고 자료

- [[문서] 스프링에서 URI 조립을 위한 UriComponentsBuilder](https://blog.naver.com/PostView.naver?blogId=aservmz&logNo=222322019981&parentCategoryNo=&categoryNo=&viewDate=&isShowPopularPosts=false&from=postView)
- [[문서] 무분별한 커스텀 예외 정의](https://ecsimsw.tistory.com/entry/%EC%98%88%EC%99%B8%EB%A5%BC-%EC%84%A0%ED%83%9D%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95)
- [[문서] Java Exception 종류와 발생 원인](https://namsancha.tistory.com/2)