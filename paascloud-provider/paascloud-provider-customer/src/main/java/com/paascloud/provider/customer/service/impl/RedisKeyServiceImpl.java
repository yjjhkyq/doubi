package com.paascloud.provider.customer.service.impl;

import cn.hutool.core.util.StrUtil;
import com.paascloud.core.constant.Constants;
import com.paascloud.provider.customer.service.RedisKeyService;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyServiceImpl implements RedisKeyService {
    private static final String KEY_PREFIX = "Customer";

    private static final String FULL_KEY = KEY_PREFIX + ":{}";

    private static final String CUSTOMER_BY_USER_NAME = "Customer:UserName:{}";
    public static final String CUSTOMER_BY_ID = "Customer:Id:{}";
    public static final String CUSTOMER_LOGININFO_BY_TOKEN = "Customer:LoginInfo:Token:{}";

    public static final String ROLE_BY_SYSTEM_NAME = "Role:SystemName:{}";

    public static final String CUSTOMER_ROLE_BY_CUSTOMERID = "CustomerRole:CustomerId:{}";

    public static final String CUSTOMER_PASSWORD_BY_CUSTOMERID = "CustomerPassword:CustomerId:{}";

    public static final String CUSTOMER_RELATION = "CustomerRelation:FromCustomerId:{}:ToCustomerId:{}";
    public static final String CUSTOMER_RELATION_FOLLOW = "Customer:Relation:Follow:{}";
    public static final String CUSTOMER_RELATION_FANS = "Customer:Relation:Fans:{}";

    public static final String GENERIC_ATTRIBUTE = "Generic:Attribute:KeyGroup:{}:EntityId:{}";
    public static final String GENERIC_ATTRIBUTE_HASH = "Generic:Attribute:Hash:KeyGroup:{}:EntityId:{}";

    @Override
    public String getCustomerKey(String customerUserName) {
        return getFullKey(CUSTOMER_BY_USER_NAME, customerUserName);
    }

    @Override
    public String getCustomerKey(long customerId) {
        return getFullKey(CUSTOMER_BY_ID, customerId);
    }

    @Override
    public String getCustomerLoginInfoKey(String token) {
        return getFullKey(CUSTOMER_LOGININFO_BY_TOKEN, token);
    }

    @Override
    public String getRoleKey(String customerSystemName) {
        return getFullKey(ROLE_BY_SYSTEM_NAME, customerSystemName);
    }

    @Override
    public String getCustomerRoleKey(long customerId) {
        return getFullKey(CUSTOMER_ROLE_BY_CUSTOMERID, customerId);
    }

    @Override
    public String getCustomerPasswordKey(long customerId) {
        return getFullKey(CUSTOMER_PASSWORD_BY_CUSTOMERID, customerId);
    }

    @Override
    public String getCustomerRelationOfFollowKey(long customerId) {
        return getFullKey(CUSTOMER_RELATION_FOLLOW, customerId);
    }

    @Override
    public String getCustomerRelationOfFansKey(long customerId) {
        return getFullKey(CUSTOMER_RELATION_FANS, customerId);
    }

    @Override
    public String getCustomerRelationKey(long fromCustomerId, long toCustomerId) {
        return getFullKey(CUSTOMER_RELATION, fromCustomerId, toCustomerId);
    }

    @Override
    public String getGenericAttributeKey(long entityId, String keyGroup) {
        return getFullKey(GENERIC_ATTRIBUTE, keyGroup, entityId);
    }

    @Override
    public String getGenricAttributeHashKey(long entityId, String keyGroup) {
        return getFullKey(GENERIC_ATTRIBUTE_HASH, keyGroup, entityId);
    }

    @Override
    public String getGlobalAttributeKeyGroup(String localKeyGroup) {
        return getFullKey("{}{}", Constants.REDIS_KEY_SPLITTER, localKeyGroup);
    }

    @Override
    public String getLocalAttributeKeyGroup(String globalKeyGroup) {
        return globalKeyGroup.substring(globalKeyGroup.indexOf(Constants.REDIS_KEY_SPLITTER));
    }

    private String getFullKey(CharSequence keyTemplate, Object... params){
        return StrUtil.format(FULL_KEY, StrUtil.format(keyTemplate, params));
    }
}
