package com.restful.web.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    private UserService userService; // di

    // Constructor, @Autowired...
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/user/{id}")
    public User findById(@PathVariable int id) {
        return userService.findById(id);
    }
}
