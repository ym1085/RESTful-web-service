```
@date   : 2022-04-25 21:31
@author : ymkim
@desc   :  
```

## 01. XML 데이터 요청을 위한 라이브러리 추가 

```groovy
implementation 'com.fasterxml.jackson.core:jackson-databind:2.0.1'
```

- XML 데이터 요청을 받기 위해 build.gradle 파일에 해당 디팬던시를 추가한다.
- 어차피 JSON으로 데이터 받아서 쓸거기 때문에 가볍게 인지하고 넘어간다.

## 02. Response 데이터 제어를 위한 Filtering

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Integer id;

    @Size(min = 2, message = "Name은 2글자 이상 입력해주세요.")
    private String userName;

    @Past
    private Date joinDate;

    private String password;
    private String ssn;
}
```

- User 클래스에 password, ssn(주민 번호) 필드를 추가 한다.
- 클라이언트에게 노출 되면 안되는 필드를 제어하는 방법을 알아볼 것이다.

```java
@Service
public class UserService {
    private static List<User> userList = new ArrayList<>();

    private static int userCnt = 3;

    // static 초기화 블럭에서, 생성자 필드 2개를 추가 한다
    static {
        userList.add(new User(1, "Kenneth", new Date(), "pass1", "930823-1065627"));
        userList.add(new User(2, "Alice", new Date(), "pass2", "910924-1065628"));
        userList.add(new User(3, "Elena", new Date(), "pass3", "680864-1065627"));
    }

    public List<User> findAll() {
        return userList;
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(++userCnt);
        }
        userList.add(user);
        return user;
    }

    public User findById(int id) {
        for (User user : userList) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public User deleteById(int id) {
        Iterator<User> iterator = userList.iterator();

        while(iterator.hasNext()) {
            User user = iterator.next();
            if (user.getId() == id) {
                iterator.remove();
                return user;
            }
        }
        return null;
    }
}
```

- 위 주석처럼 생성자 필드를 추가해준다.
- password, ssn 필드.

```json
[
    {
        "id": 1,
        "userName": "Kenneth",
        "joinDate": "2022-04-25T12:52:25.104+00:00",
        "password": "pass1",
        "ssn": "930823-1065627"
    },
    {
        "id": 2,
        "userName": "Alice",
        "joinDate": "2022-04-25T12:52:25.104+00:00",
        "password": "pass2",
        "ssn": "910924-1065628"
    },
    {
        "id": 3,
        "userName": "Elena",
        "joinDate": "2022-04-25T12:52:25.104+00:00",
        "password": "pass3",
        "ssn": "680864-1065627"
    }
]
```

- User 클래스를 수정한 후 나오는 JSON 결과값은 위와 같다.
- 하지만 비밀번호, 주민등록번호는 보안상 나오면 안되는 값이다.
- 다음은 클라이언트에게 노출이 되면 안되는 정보를 어떻게 처리해야 할까?
  - 930823-******* 와 같은 값을 넘겨준다.
  - null 값을 넘겨준다.
  - 다른 값으로 대체해서 넘겨준다.

### @JsonIgnore 추가

```java
// 필드 레벨에서 어노테이션 사용
@JsonIgnore
private String password;

@JsonIgnore
private String ssn;
```

```json
[
  {
    "id": 1,
    "userName": "Kenneth",
    "joinDate": "2022-04-25T12:59:50.037+00:00"
  },
  {
    "id": 2,
    "userName": "Alice",
    "joinDate": "2022-04-25T12:59:50.037+00:00"
  },
  {
    "id": 3,
    "userName": "Elena",
    "joinDate": "2022-04-25T12:59:50.037+00:00"
  }
]
```

- @JsonIgnore 어노테이션을 사용하면 클라이언트측에 해당 필드가 노출되지 않는다.

### @JsonIgnoreProperties

```java
@JsonIgnoreProperties(value = {"password"})
```

- 클래스 레벨에서 해당 어노테이션을 사용하여 동일하게 필드 노출을 막을 수 있다.
- { } 을 사용하여 여러 필드를 주입 받는것도 가능.

## 03. Filtering 방법 - 개별 사용자 조회

사용자에 따른 json 형태로 데이터를 보내고자 할 때 보내지 안항도 되는 데이터는 필터링 하여 보내고 싶은 경우 사용.

```java
@Data
@RequeiredArgsConstructor
@NotArgsConstructor
@JsonFilter("UserInfo")
public class User {

    private Integer id;
  
    @Size(min = 2, message = "Name은 2글자 이상 입력해주세요.")
    private String userName;
  
    @Past
    private Date joinDate;
    
    private String password;
    private String ssn;
}
```

- 위와 같이 @JsonFilter 어노테이션을 추가 해준다.
- 후에 Filter를 등록할 때 해당 아이디 값으로 참조하여 사용.

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {
    private final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    private final UserService userService;

    @GetMapping("/user")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/user/{id}")
    public MappingJacksonValue findById(@PathVariable int id) {
        log.debug("");
        User user = userService.findById(id);
        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }

        // filter 추가
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter
//                .filterOutAllExcept("id", "userName", "joinDate", "ssn");
                  .filterOutAllExcept("id", "userName", "joinDate", "ssn", "password");

        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(user);
        mapping.setFilters(filters);

        return mapping;
    }
}
```

- **SimpleBeanPropertyFilter**
  - 지정된 필드드만 **JSON**으로 변환, 알 수 없는 필드는 무시.
- **MappingJacksonValue** 클래스를 사용하여 User 클래스를 Filter를 적용할 수 있는 타입으로 변경.
- FilterProvider를 통해 @JsonFilter에서 지정한 id 값, 생성한 Filter를 매개변수로 하여 우리가 사용할 수 있는 필터로 변경.

### 참고 자료

- https://www.inflearn.com/course/spring-boot-restful-web-services/lecture/39102?tab=curriculum&volume=1.00&quality=1080
- https://jy-beak.tistory.com/123?category=980784