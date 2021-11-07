package com.x.core.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;

/**
 * 自定义异常
 * 
 */
public class ApiFeignException extends FeignException
{
    private static final long serialVersionUID = 1L;

    private long code;

    private String message;

    public ApiFeignException(long code, String message){
        super(HttpStatus.OK.value(), message);
        this.code = code;
        this.message = message;
    }



    @Override
    public String getMessage()
    {
        return message;
    }

    public long getCode()
    {
        return code;
    }
}
