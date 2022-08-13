package com.x.provider.customer.service;

import com.x.core.domain.SuggestionTypeEnum;
import com.x.provider.customer.enums.SystemCustomerAttributeName;
import com.x.provider.customer.model.ao.*;
import com.x.provider.customer.model.domain.Customer;
import com.x.provider.customer.model.domain.CustomerPassword;
import com.x.provider.customer.model.domain.GenericAttribute;
import com.x.provider.customer.model.domain.Role;
import com.x.provider.customer.model.query.CustomerAttributeQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface CustomerService {
    List<String> CUSTOMER_ATTRIBUTE_NAME_OSS = Arrays.asList(SystemCustomerAttributeName.AVATAR_ID.toString(), SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID.toString());
    void register(UserNamePasswordRegisterAO userNamePasswordRegisterAO);
    String loginByPassword(LoginByPasswordAO userNamePasswordLoginAO);
    String loginOrRegisterBySms(LoginOrRegBySmsAO loginOrRegByPhoneAO);
    void checkPhoneBound(long customerId, ValidatePhoneAO validatePhoneAO);
    void bindPhone(long customerId, BindPhoneAO bindPhoneAO);
    void changePassword(long customerId, ChangePasswordByOldPasswordAO changePasswordAO);
    void changePhone(long customerId, ChangePhoneAO changePhoneAO);
    void changeUserName(long customerId, ChangeUserNameAO changeUserNameAO);
    Role getRole(String systemName);
    Customer getCustomer(String userName);
    Customer getCustomer(long id);
    CustomerPassword getCustomerPassword(long customerId);
    List<Role> listCustomerRole(long customerId);
    void setCustomerAttribute(long customerId, SystemCustomerAttributeName systemCustomerAttributeName, String value);

    void onCustomerDraftAttributeGreenFinished(long customerId, SystemCustomerAttributeName attributeName, String value, SuggestionTypeEnum suggestionTypeEnum);
    Customer registerCustomer(Customer customer);
    List<Customer> listCustomer(List<Long> idList);
    Map<Long, List<GenericAttribute>> listCustomerAttribute(CustomerAttributeQuery customerAttributeQuery);
    Map<Long, List<GenericAttribute>> listAndFillDefaultCustomerAttribute(CustomerAttributeQuery customerAttributeQuery);
}
