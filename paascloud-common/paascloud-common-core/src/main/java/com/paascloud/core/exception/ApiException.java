package com.paascloud.core.exception;

import com.paascloud.core.web.api.IErrorCode;

/**
 * 自定义异常
 * 
 */
public class ApiException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private long code;

    private String message;

    public ApiException(String message)
    {
        this.message = message;
    }

    public ApiException(String message, long code)
    {
        this.message = message;
        this.code = code;
    }

    public ApiException(long code, String message)
    {
        this.message = message;
        this.code = code;
    }

    public ApiException(IErrorCode errorCode){
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ApiException(String message, Throwable e)
    {
        super(message, e);
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
