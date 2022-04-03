package com.restful.web.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;

@Data
@AllArgsConstructor
public class User {
    private Integer id;
    private String userName;
    private Date joinDate;
}
