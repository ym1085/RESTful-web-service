package com.restful.web.user;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class UserService {
    private static List<User> userList = new ArrayList<>();

    private static int userCnt = 3;

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
