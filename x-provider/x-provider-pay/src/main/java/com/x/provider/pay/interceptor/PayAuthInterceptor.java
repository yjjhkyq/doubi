package com.x.provider.pay.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.x.core.constant.Constants;
import com.x.core.exception.ApiException;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.SecurityUtils;
import com.x.provider.pay.annotation.PayToken;
import com.x.provider.pay.enums.PayResultCode;
import com.x.provider.pay.service.PasswordEncoderService;
import com.x.provider.pay.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class PayAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private PasswordEncoderService passwordEncoderService;
    @Autowired
    private WalletService walletService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 判断是不是我们自己写的前台的方法
        if (!method.getDeclaringClass().getPackage().getName().equals("com.x.provider.pay.controller.frontend")) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        //检查是否有PayToken注解，有要进行验证
        if (method.isAnnotationPresent(PayToken.class)) {
            // 需要验证的情况
            PayToken payToken = method.getAnnotation(PayToken.class);
            if (!payToken.required()) {
                return HandlerInterceptor.super.preHandle(request, response, handler);
            }

            long customerId = SecurityUtils.getCurrentCustomerId();

            String token = request.getHeader(Constants.HTTP_HEADER_PAY_TOKEN);
            if (ObjectUtil.isEmpty(token)) {
                token = request.getParameter(Constants.HTTP_HEADER_PAY_TOKEN);
                if (ObjectUtil.isEmpty(token)) {
                    throw new ApiException(PayResultCode.USER_PAY_TOKEN_NOT_FOUND);
                }
            }
//            ApiAssetUtil.isTrue(walletService.validateWalletToken(token, customerId), PayResultCode.USER_PAY_TOKEN_INVALID);
            if (walletService.validateWalletToken(token, customerId)) {
                // 执行
                return HandlerInterceptor.super.preHandle(request, response, handler);
            } else {
                // 失败
                throw new ApiException(PayResultCode.USER_PAY_TOKEN_INVALID);
            }
        }
        //不需要验证的情况
        return HandlerInterceptor.super.preHandle(request, response, handler);

    }
}
