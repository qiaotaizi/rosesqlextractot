package com.jaiz.utils.exceptions;

/**
 * 注解中sql为空时抛出此异常
 */
public class SQLNotFoundException extends Exception{

    public SQLNotFoundException(String s){
        super(s);
    }

}
