package com.x.core.exception;

import com.x.core.web.api.IErrorCode;

/**
 * 自定义异常
 * 
 */
public class CheckedApiException extends Exception
{
    private static final long serialVersionUID = 1L;

    private long code;

    private String message;

    public CheckedApiException(String message)
    {
        this.message = message;
    }

    public CheckedApiException(String message, long code)
    {
        this.message = message;
        this.code = code;
    }

    public CheckedApiException(long code, String message)
    {
        this.message = message;
        this.code = code;
    }

    public CheckedApiException(IErrorCode errorCode){
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public CheckedApiException(String message, Throwable e)
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
