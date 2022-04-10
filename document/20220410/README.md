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

## 참고 자료

- [[문서] 스프링에서 URI 조립을 위한 UriComponentsBuilder](https://blog.naver.com/PostView.naver?blogId=aservmz&logNo=222322019981&parentCategoryNo=&categoryNo=&viewDate=&isShowPopularPosts=false&from=postView)