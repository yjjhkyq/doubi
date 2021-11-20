package com.x.provider.mc.service.impl;

import cn.hutool.http.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.x.core.utils.JsonUtil;
import com.x.provider.mc.service.CentrifugoApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author: liushenyi
 * @date: 2021/10/19/10:56
 */
@Slf4j
@Service
public class CentrifugoApiServiceImpl implements CentrifugoApiService {

    @Override
    public String authenticationToken(String tokenHmacSecretKey, long expireAt, String subject) {
        Algorithm algorithm = Algorithm.HMAC256(tokenHmacSecretKey);
        return JWT.create().withExpiresAt(new Date(expireAt))
                .withSubject(subject)
                .sign(algorithm)
                ;
    }

    @Override
    public void publish(String url, String apiKey, String channel, Map<String, Object> data) {
        Map<String, Object> params = new HashMap<>();
        params.put("channel", channel);
        params.put("data",data);
        request(url, apiKey, "publish", params);
        return;
    }

    @Override
    public List<String> listChannels(String url, String apiKey) {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> requestResult = request(url, apiKey, "channels", params);
        if (requestResult.containsKey("channels")){
            return ((ArrayList<String>)requestResult.get("channels"));
        }
        return new ArrayList<>();
    }

    private Map<String, Object> request(String url, String apiKey, String method, Map<String, Object> params) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("method",method);
        postParams.put("params",params);
        HttpRequest request = HttpUtil.createRequest(Method.POST, url);
        request.auth("apikey " + apiKey);
        request.body(JsonUtil.toJSONString(postParams));
        request.header(Header.CONTENT_TYPE, ContentType.JSON.getValue());
        String body = request.execute().body();
        CentrifugoResult centrifugoResult = JsonUtil.parseObject(body, CentrifugoResult.class);
        if (!centrifugoResult.success()){
            log.error("request centrifugo error: url: {} api key: {} method: {} error code : {} error msg: {}",
                    url, apiKey, method, centrifugoResult.getError().getCode(), centrifugoResult.getError().getMessage());
            throw new RuntimeException("request centrifugo error");
        }
        return centrifugoResult.getResult();
    }

    private Map<String,String> prepareHeaders(String apiKey) {
        return new HashMap<>() {{
            put("Content-Type", "application/json");
            put("Authorization", "apikey " + apiKey);
        }};
    }

    private static class CentrifugoResult extends LinkedHashMap {

        public CentrifugoResult(){

        }
        public boolean success(){
            return !this.containsKey("error");
        }

        public Error getError(){
            Map<String, Object> errorMap = (Map<String, Object>)this.get("error");
            Error error = new Error();
            error.setCode(Integer.parseInt(String.valueOf(errorMap.get("code"))));
            error.setMessage(String.valueOf(errorMap.get("message")));
            return error;
        }

        public Map<String, Object> getResult(){
            if (this.get("result") != null){
                return (Map<String, Object>) this.get("result");
            }
            return new LinkedHashMap<>();
        }

        private class Error{
            private int code;
            private String message;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }
        }
    }
}
