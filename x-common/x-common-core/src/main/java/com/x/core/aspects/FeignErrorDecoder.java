package com.x.core.aspects;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.x.core.exception.ApiException;
import com.x.core.exception.ApiFeignException;
import com.x.core.utils.JsonUtil;
import com.x.core.web.api.R;
import com.x.core.web.api.ResultCode;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        try {
        String body = Util.toString(response.body().asReader(Charset.defaultCharset()));
        return response.status() == HttpStatus.INTERNAL_SERVER_ERROR.value() ? JsonUtil.parseObject(body, ApiException.class)
                : JsonUtil.parseObject(body, Exception.class);
        }
        catch (Exception e){
            return e;
        }
    }
}
