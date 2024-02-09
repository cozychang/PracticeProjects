package com.reggie.common;


//自定义异常类
public class CustomerException extends RuntimeException{
    public CustomerException (String message){
        super(message);
    }
}
