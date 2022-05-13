package com.x.provider.customer.service.impl;

import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.IdUtils;
import com.x.core.utils.SecurityUtils;
import com.x.core.utils.ServletUtils;
import com.x.core.web.api.ResultCode;
import com.x.provider.customer.model.domain.Customer;
import com.x.provider.customer.service.AuthenticationService;
import com.x.provider.customer.service.RedisKeyService;
import com.x.redis.service.RedisService;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final Duration TOKEN_EXPIRED_DURATION = Duration.ofDays(100);

    private final RedisKeyService redisKeyService;
    private final RedisService redisService;

    public AuthenticationServiceImpl(RedisKeyService redisKeyService,
                                     RedisService redisService){
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
    }

    @Override
    public String signIn(Customer customer) {
        String token = IdUtils.fastSimpleUUID();
        redisService.setCacheObject(redisKeyService.getCustomerLoginInfoKey(token), Long.valueOf(customer.getId()), TOKEN_EXPIRED_DURATION);
        return token;
    }

    @Override
    public void signOut() {
        redisService.deleteObject(SecurityUtils.getBearAuthorizationToken());
    }

    @Override
    public long getAuthenticatedCustomerId(String token) {
        Long customerId = redisService.getLongCacheObject(redisKeyService.getCustomerLoginInfoKey(token));
        if (customerId != null){
            redisService.expire(redisKeyService.getCustomerLoginInfoKey(token), TOKEN_EXPIRED_DURATION);
        }
        ApiAssetUtil.notNull(customerId, ResultCode.UNAUTHORIZED);
        return customerId;
    }
}
