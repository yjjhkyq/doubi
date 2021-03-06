package com.x.core.aspects;

import com.x.core.exception.ApiException;
import com.x.core.exception.ApiFeignException;
import com.x.core.web.api.R;
import com.x.core.web.api.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    /**
     * 请求方式不支持
     */
    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R handleException(HttpRequestMethodNotSupportedException e)
    {
        log.error(e.getMessage(), e);
        return R.fail("不支持' " + e.getMethod() + "'请求");
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R notFount(RuntimeException e)
    {
        log.error("运行时异常:", e);
        return R.fail("运行时异常:" + e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R handleException(Exception e)
    {
        log.error(e.getMessage(), e);
        return R.fail("服务器错误，请联系管理员");
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ApiFeignException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R businessException(ApiFeignException e)
    {
        log.error("api feign exception {} {}", e.getCode(), e.getMessage());
        return R.build(e.getCode(), e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R businessException(ApiException e)
    {
        log.error("api exception {} {}", e.getCode(), e.getMessage());
        return R.build(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        List<ObjectError> errors =e.getBindingResult().getAllErrors();
        StringBuffer errorMsg=new StringBuffer();
        errors.stream().forEach(x -> errorMsg.append(x.getDefaultMessage()).append(";"));
        return R.build(ResultCode.VALIDATE_FAILED, errors);
    }
}
