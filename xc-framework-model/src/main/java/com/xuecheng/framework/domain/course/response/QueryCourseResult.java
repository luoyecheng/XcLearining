package com.xuecheng.framework.domain.course.response;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;

@Data
@ToString
public class QueryCourseResult {
    @Id
    private String id;
    private String name;
    private String pic;
}
