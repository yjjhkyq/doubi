package com.x.core.aspects;

import com.x.core.exception.ApiException;
import com.x.core.utils.JsonUtil;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.nio.charset.Charset;

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
