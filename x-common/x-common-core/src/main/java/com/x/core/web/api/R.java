package com.x.core.web.api;


import java.io.Serializable;

/**
 * 响应信息主体
 *
 */
public class R<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long code;

    private String message;

    private T data;

    public static <T> R<T> ok()
    {
        return restResult(null, ResultCode.SUCCESS);
    }

    public static <T> R<T> ok(T data)
    {
        return restResult(data, ResultCode.SUCCESS);
    }

    public static <T> R<T> fail()
    {
        return restResult(null, ResultCode.FAILED);
    }

    public static <T> R<T> fail(String msg)
    {
        return restResult(null, ResultCode.FAILED);
    }

    public static <T> R<T> fail(T data)
    {
        return restResult(data, ResultCode.FAILED);
    }

    public static <T> R<T> build(long code, String msg)
    {
        return restResult(null, code, msg);
    }

    public static <T> R<T> build(IErrorCode errorCode, T t)
    {

        return restResult(t, errorCode);
    }
    public static R<Void> build(IErrorCode errorCode)
    {
        return restResult(null, errorCode);
    }

    private static <T> R<T> restResult(T data, long code, String msg)
    {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMessage(msg);
        return apiResult;
    }

    private static <T> R<T> restResult(T data, IErrorCode errorCode)
    {
        R<T> apiResult = new R<>();
        apiResult.setCode(errorCode.getCode());
        apiResult.setData(data);
        apiResult.setMessage(errorCode.getMessage());
        return apiResult;
    }

    public long getCode()
    {
        return code;
    }

    public void setCode(long code)
    {
        this.code = code;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public boolean isOk(){
        return code == ResultCode.SUCCESS.getCode();
    }
}
