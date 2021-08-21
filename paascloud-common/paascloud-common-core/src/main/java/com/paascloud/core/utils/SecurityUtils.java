package com.paascloud.core.utils;

import com.paascloud.core.constant.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.Optional;

public class SecurityUtils {
    public static long getCurrentCustomerId(){
        String userId = Optional.ofNullable(ServletUtils.getRequest().getHeader(Constants.HTTP_HEADER_CUSTOMER_ID)).orElse("0");
        return Long.parseLong(userId);
    }

    public static String getBearAuthorizationToken(){
        String bearAuthorization =  ServletUtils.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(bearAuthorization)){
            return null;
        }
        return bearAuthorization.substring(Constants.BEAR.length());
    }
}
