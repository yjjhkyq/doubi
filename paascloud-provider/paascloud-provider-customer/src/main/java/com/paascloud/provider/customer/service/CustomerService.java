package com.paascloud.provider.customer.service;

import com.paascloud.provider.api.oss.enums.SuggestionTypeEnum;
import com.paascloud.provider.customer.enums.SystemCustomerAttributeName;
import com.paascloud.provider.customer.model.ao.ChangePasswordByOldPasswordAO;
import com.paascloud.provider.customer.model.ao.ChangeUserNameAO;
import com.paascloud.provider.customer.model.ao.UserNamePasswordLoginAO;
import com.paascloud.provider.customer.model.ao.UserNamePasswordRegisterAO;
import com.paascloud.provider.customer.model.domain.Customer;
import com.paascloud.provider.customer.model.domain.CustomerPassword;
import com.paascloud.provider.customer.model.domain.Role;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface CustomerService {
    List<String> MEDIA_CUSTOMER_ATTRIBUTE_NAME = Arrays.asList(SystemCustomerAttributeName.AVATAR_ID.toString(), SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID.toString());
    void register(UserNamePasswordRegisterAO userNamePasswordRegisterAO);
    String login(UserNamePasswordLoginAO userNamePasswordLoginAO);
    void logout(String token);
    long validateToken(String token);
    void changePassword(long customerId, ChangePasswordByOldPasswordAO changePasswordAO);
    void changeUserName(long customerId, ChangeUserNameAO changeUserNameAO);
    Role getRole(String systemName);
    Customer getCustomer(String userName);
    Customer getCustomer(long id);
    CustomerPassword getCustomerPassword(long customerId);
    List<Role> listCustomerRole(long customerId);
    void setCustomerDraftAttribute(long customerId, SystemCustomerAttributeName systemCustomerAttributeName, String value);
    Map<String, String> listCustomerAttribute(long customerId);
    Map<String, String> listCustomerAttribute(long customerId, List<SystemCustomerAttributeName> attributeNames);
    void onCustomerDraftAttributeGreenFinshed(long customerId, SystemCustomerAttributeName attributeName, String value, SuggestionTypeEnum suggestionTypeEnum);
}
