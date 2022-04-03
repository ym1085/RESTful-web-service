package com.restful.web.user;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    private static List<User> userList = new ArrayList<>();

    private static int userCnt = 3;

    static {
        userList.add(new User(1, "Kenneth", new Date()));
        userList.add(new User(2, "Alice", new Date()));
        userList.add(new User(3, "Elena", new Date()));
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
}
