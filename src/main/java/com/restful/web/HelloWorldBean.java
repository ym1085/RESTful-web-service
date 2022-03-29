package com.restful.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
public class HelloWorldBean {
    private String message;
    private Date createDate;
    private Date updateDate;

    /*@Data
    public class InnerHelloWorldBean {
        private int age;
        private String name;
        private String address;
    }*/
}
