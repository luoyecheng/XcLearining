package com.xuecheng.manage_course.ecxeption;

import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
//import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class CustomException extends ExceptionCatch {
    static {
        //builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);
    }
}
