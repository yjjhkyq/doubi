package com.x.provider.customer.service;

public interface RedisKeyService {
    //Customer
    String getCustomerKey(String customerUserName);
    String getCustomerKey(long customerId);
    String getCustomerLoginInfoKey(String token);

    //Role
    String getRoleKey(String roleSystemName);

    //CustomerRole
    String getCustomerRoleKey(long customerId);

    //CustomerPassword
    String getCustomerPasswordKey(long customerId);

    //CustomerRelation
    String getCustomerRelationOfFollowKey(long customerId);
    String getCustomerRelationOfFansKey(long customerId);
    String getCustomerRelationKey(long fromCustomerId, long toCustomerId);

    //GenericAttribute
    String getGenericAttributeKey(long entityId, String keyGroup);
    String getGenricAttributeHashKey(long entityId, String keyGroup);
    String getGlobalAttributeKeyGroup(String localKeyGroup);
    String getLocalAttributeKeyGroup(String globalKeyGroup);
}
