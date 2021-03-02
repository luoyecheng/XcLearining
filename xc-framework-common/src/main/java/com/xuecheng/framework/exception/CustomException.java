package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

//自定义异常
public class CustomException extends RuntimeException{
    ResultCode resultCode;
    public CustomException(ResultCode resultCode)
    {
        this.resultCode=resultCode;
    }
    public ResultCode getResultCode()
    {
        return resultCode;
    }
}
