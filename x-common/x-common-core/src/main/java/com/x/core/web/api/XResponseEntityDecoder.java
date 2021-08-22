package com.x.core.web.api;

import com.x.core.exception.ApiException;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class XResponseEntityDecoder extends ResponseEntityDecoder {

    public XResponseEntityDecoder(Decoder decoder) {
        super(decoder);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        Object result = super.decode(response, type);
        if (result instanceof R){
            R rpcResult = (R)result;
            if (!rpcResult.isOk()){
                throw new ApiException(rpcResult.getCode(), rpcResult.getMessage());
            }
        }
        return result;
    }
}
