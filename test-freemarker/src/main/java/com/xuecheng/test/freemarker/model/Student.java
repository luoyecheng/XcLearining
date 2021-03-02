package com.xuecheng.test.freemarker.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class Student {
    private String name;
    private int age;
    private Date birthday;
    private float money;
    private List<Student> friends;
    private Student bestFriend;
}
