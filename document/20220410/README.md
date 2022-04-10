```
@date   : 2022-04-10 13:45
@author : ymkim
@desc   : HTTP Status Code 제어와 Exception Handling 
```

## 01. User Service API 구현

### ServletUriComponentsBuilder

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

### HTTP Status Code Exception Handling

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
            throw new UserNotFoundException(String.format("ID[%s] not found", id)); // exception 클래스 생성
        }
        return user;
    }
}
```

```java
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}

```

- 위와 같이 간단한 예외 클래스(UserNotFoundException)를 생성하여 해당 예외를 처리한다.
- 이 때 Trace log에 노출되면 안되는 내용이 존재 하는데 해당 내용은 다음을 살펴보자.

```java
// HTTP Status code
// 2XX -> OK
// 4XX -> Client Error [Auth..]
// 5XX -> Server Error [Server program..]
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
```

- @ResponseStatus(HttpStatus.NOT_FOUND)를 사용하여 요청에 맞는 응답 예외를 반환.
- 서버 측 Error가 아니라, 사용자가 요청한 Resource 가 존재하지 않기에 404 Error 반환.

## 참고 자료

- [[문서] 스프링에서 URI 조립을 위한 UriComponentsBuilder](https://blog.naver.com/PostView.naver?blogId=aservmz&logNo=222322019981&parentCategoryNo=&categoryNo=&viewDate=&isShowPopularPosts=false&from=postView)