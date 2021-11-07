package com.x.provider.customer.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.cache.event.EntityChangedEventBus;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.IdUtils;
import com.x.core.web.api.R;
import com.x.core.web.api.ResultCode;
import com.x.provider.api.general.model.ao.ValidateVerificationCodeAO;
import com.x.provider.api.general.service.SmsRpcService;
import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import com.x.provider.api.oss.enums.SuggestionTypeEnum;
import com.x.provider.api.oss.model.ao.AttributeGreenRpcAO;
import com.x.provider.api.oss.model.dto.AttributeGreenResultDTO;
import com.x.provider.api.oss.service.GreenRpcService;
import com.x.provider.customer.configure.ApplicationConfig;
import com.x.provider.customer.constant.CustomerConstants;
import com.x.provider.customer.enums.AttributeKeyGroupEnum;
import com.x.provider.customer.enums.SystemCustomerAttributeName;
import com.x.provider.customer.enums.SystemRoleNameEnum;
import com.x.provider.customer.enums.UserResultCode;
import com.x.provider.customer.mapper.CustomerMapper;
import com.x.provider.customer.mapper.CustomerPasswordMapper;
import com.x.provider.customer.mapper.CustomerRoleMapper;
import com.x.provider.customer.mapper.RoleMapper;
import com.x.provider.customer.model.ao.*;
import com.x.provider.customer.model.domain.Customer;
import com.x.provider.customer.model.domain.CustomerPassword;
import com.x.provider.customer.model.domain.CustomerRole;
import com.x.provider.customer.model.domain.Role;
import com.x.provider.customer.service.CustomerService;
import com.x.provider.customer.service.GenericAttributeService;
import com.x.provider.customer.service.PasswordEncoderService;
import com.x.provider.customer.service.RedisKeyService;
import com.x.provider.customer.service.cache.customer.CustomerChangedEvent;
import com.x.provider.customer.service.cache.customer.CustomerPasswordChangedEvent;
import com.x.provider.customer.service.cache.customer.CustomerRoleChangedEvent;
import com.x.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final Duration TOKEN_EXPIRED_DURATION = Duration.ofDays(100);

    private final RedisKeyService redisKeyService;
    private final RedisService redisService;
    private final CustomerMapper customerMapper;
    private final CustomerPasswordMapper customerPasswordMapper;
    private final CustomerRoleMapper customerRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoderService passwordEncoderService;
    private final EntityChangedEventBus entityChangedEventBus;
    private final GenericAttributeService genericAttributeService;
    private final GreenRpcService greenRpcService;
    private final ApplicationConfig applicationConfig;
    private final SmsRpcService smsRpcService;

    public CustomerServiceImpl(RedisKeyService redisKeyService,
                               RedisService redisService,
                               CustomerMapper customerMapper,
                               CustomerPasswordMapper customerPasswordMapper,
                               CustomerRoleMapper customerRoleMapper,
                               RoleMapper roleMapper,
                               PasswordEncoderService passwordEncoderService,
                               EntityChangedEventBus entityChangedEventBus,
                               GenericAttributeService genericAttributeService,
                               GreenRpcService greenRpcService,
                               ApplicationConfig applicationConfig,
                               SmsRpcService smsRpcService){
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
        this.customerMapper =customerMapper;
        this.customerPasswordMapper = customerPasswordMapper;
        this.customerRoleMapper = customerRoleMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoderService = passwordEncoderService;
        this.entityChangedEventBus = entityChangedEventBus;
        this.genericAttributeService = genericAttributeService;
        this.greenRpcService = greenRpcService;
        this.applicationConfig = applicationConfig;
        this.smsRpcService = smsRpcService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserNamePasswordRegisterAO userNamePasswordRegisterAO) {
        Customer customerExisted = getCustomer(userNamePasswordRegisterAO.getUserName());
        ApiAssetUtil.isNull(customerExisted, UserResultCode.USER_NAME_EXISTED);
        Customer customer = new Customer(userNamePasswordRegisterAO.getUserName());
        customerMapper.insert(customer);
        CustomerPassword customerPassword = new CustomerPassword(customer.getId(), RandomUtil.randomNumbers(4));
        customerPassword.setPassword(passwordEncoderService.encode(userNamePasswordRegisterAO.getPassword(), customerPassword.getPasswordSalt()));
        customerPasswordMapper.insert(customerPassword);
        Role registeredRole = getRole(SystemRoleNameEnum.REGISTERED.name());
        CustomerRole customerRole = new CustomerRole(customer.getId(), registeredRole.getId());
        customerRoleMapper.insert(customerRole);
        entityChangedEventBus.postEntityInserted(new CustomerChangedEvent(customer));
        entityChangedEventBus.postEntityInserted(new CustomerPasswordChangedEvent(customerPassword));
        entityChangedEventBus.postEntityInserted(new CustomerRoleChangedEvent(customerRole));
    }

    @Override
    public String loginByPassword(LoginByPasswordAO userNamePasswordLoginAO) {
        Customer customer = getCustomer(0L, userNamePasswordLoginAO.getUserName(), userNamePasswordLoginAO.getPhone(), null);
        ApiAssetUtil.notNull(customer, UserResultCode.USER_NAME_OR_PWD_ERROR);
        ApiAssetUtil.isTrue(customer.isActive(), UserResultCode.CUSTOMER_NOT_ACTIVE);
        CustomerPassword customerPassword = getCustomerPassword(customer.getId());
        ApiAssetUtil.isTrue(passwordEncoderService.matches(userNamePasswordLoginAO.getPassword(), customerPassword.getPasswordSalt(), customerPassword.getPassword()), UserResultCode.USER_NAME_OR_PWD_ERROR);
        String token = IdUtils.fastSimpleUUID();
        redisService.setCacheObject(redisKeyService.getCustomerLoginInfoKey(token), Long.valueOf(customer.getId()), TOKEN_EXPIRED_DURATION);
        return token;
    }

    @Override
    public String loginOrRegisterBySms(LoginOrRegBySmsAO loginOrRegByPhoneAO) {
        smsRpcService.validateVerificationCode(ValidateVerificationCodeAO.builder().phoneNumber(loginOrRegByPhoneAO.getPhoneNumber()).sms(loginOrRegByPhoneAO.getSmsVerificationCode()).build());
        Customer customer = getCustomerByPhone(loginOrRegByPhoneAO.getPhoneNumber());
        if (customer == null){
            customer = Customer.builder().phone(loginOrRegByPhoneAO.getPhoneNumber()).build();
            customerMapper.insert(customer);
            Role registeredRole = getRole(SystemRoleNameEnum.REGISTERED.name());
            CustomerRole customerRole = new CustomerRole(customer.getId(), registeredRole.getId());
            customerRoleMapper.insert(customerRole);
            entityChangedEventBus.postEntityInserted(new CustomerChangedEvent(customer));
            entityChangedEventBus.postEntityInserted(new CustomerRoleChangedEvent(customerRole));
        }
        String token = IdUtils.fastSimpleUUID();
        redisService.setCacheObject(redisKeyService.getCustomerLoginInfoKey(token), Long.valueOf(customer.getId()), TOKEN_EXPIRED_DURATION);
        return token;
    }

    @Override
    public void logout(String token) {
        redisService.deleteObject(token);
    }

    @Override
    public long validateToken(String token) {
        Long customerId = redisService.getLongCacheObject(redisKeyService.getCustomerLoginInfoKey(token));
        if (customerId != null){
            redisService.expire(redisKeyService.getCustomerLoginInfoKey(token), TOKEN_EXPIRED_DURATION);
        }
        ApiAssetUtil.notNull(customerId, ResultCode.UNAUTHORIZED);
        return customerId;
    }

    @Override
    public void changePassword(long customerId, ChangePasswordByOldPasswordAO changePasswordAO) {
        CustomerPassword customerPassword = getCustomerPassword(customerId);
        if (customerPassword == null){
            customerPassword = new CustomerPassword(customerId, RandomUtil.randomNumbers(4));
            customerPassword.setPassword(passwordEncoderService.encode(changePasswordAO.getNewPassword(), customerPassword.getPasswordSalt()));
            customerPasswordMapper.insert(customerPassword);
        }
        else {
            customerPassword.setPassword(passwordEncoderService.encode(changePasswordAO.getNewPassword(), customerPassword.getPasswordSalt()));
            customerPasswordMapper.updateById(customerPassword);
        }
        entityChangedEventBus.postEntityUpdated(new CustomerPasswordChangedEvent(customerPassword));
    }

    @Override
    public void changeUserName(long customerId, ChangeUserNameAO changeUserNameAO) {
        final Customer customer = getCustomer(customerId);
        Customer customerExisted = getCustomer(changeUserNameAO.getUserName());
        ApiAssetUtil.isNull(customerExisted, UserResultCode.USER_NAME_EXISTED);
        customer.setUserName(changeUserNameAO.getUserName());
        customerMapper.updateById(customer);
        entityChangedEventBus.postEntityUpdated(new CustomerChangedEvent(customer));
    }


    @Override
    public Customer getCustomer(String userName) {
        return redisService.getCacheObject(redisKeyService.getCustomerKey(userName), () ->
            getCustomer(0, userName, null, null));
    }

    @Override
    public Customer getCustomer(long id) {
        return redisService.getCacheObject(redisKeyService.getCustomerKey(id), () ->
            getCustomer(id, null, null, null));
    }

    @Override
    public CustomerPassword getCustomerPassword(long customerId) {
        return redisService.getCacheObject(redisKeyService.getCustomerPasswordKey(customerId), () ->
                getCustomerPassword(0, customerId));
    }

    @Override
    public List<Role> listCustomerRole(long customerId) {
        return redisService.getCacheObject(redisKeyService.getCustomerRoleKey(customerId), () -> {
            List<CustomerRole> customerRoles = customerRoleMapper.selectList(new LambdaQueryWrapper<CustomerRole>().eq(CustomerRole::getCustomerId, customerId));
            return roleMapper.selectBatchIds(customerRoles.stream().map(CustomerRole::getRoleId).collect(Collectors.toList()));
        });
    }

    @Override
    public void setCustomerDraftAttribute(long customerId, SystemCustomerAttributeName attributeName, String value) {
        switch (attributeName){
            case NICK_NAME:
            case SIGNATURE:
                R<AttributeGreenResultDTO> greenResult = greenRpcService.greenAttributeSync(new AttributeGreenRpcAO(redisKeyService.getGlobalAttributeKeyGroup(AttributeKeyGroupEnum.CUSTOMER.toString()),
                        customerId, attributeName.toString(), value, GreenDataTypeEnum.TEXT));
                ApiAssetUtil.isTrue(SuggestionTypeEnum.valueOf(greenResult.getData().getSuggestionType()).equals(SuggestionTypeEnum.PASS), ResultCode.GREEN_BLOCKED);
                genericAttributeService.addOrUpdateAttribute(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, attributeName.toString(), value);
                break;
            case AVATAR_ID:
            case PERSONAL_HOMEPAGE_BACKGROUND_ID:
                setCustomerMediaAttribute(customerId, attributeName, value);
                break;
        }
    }

    private void setCustomerMediaAttribute(long customerId, SystemCustomerAttributeName attributeName, String value) {
        genericAttributeService.addOrUpdateDraftAttribute(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, attributeName.toString(), value);
        try {
            greenRpcService.greenAttributeAsync(new AttributeGreenRpcAO(redisKeyService.getGlobalAttributeKeyGroup(AttributeKeyGroupEnum.CUSTOMER.toString()),
                    customerId, attributeName.toString(), value, GreenDataTypeEnum.PICTURE, CustomerConstants.CUSTOMER_ATTRIBUTE_GREEN_CALLBACK_RUL));
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
            genericAttributeService.deleteDraftAttribute(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, attributeName.toString());
            throw e;
        }
    }

    @Override
    public Map<String, String> listCustomerAttribute(long customerId) {
        Map<String, String> attributeMap = genericAttributeService.listAttributeMap(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId);
        fillDefaultAttribute(attributeMap);
        return attributeMap;
    }

    @Override
    public Map<String, String> listCustomerAttribute(long customerId, List<SystemCustomerAttributeName> attributeNames) {
        Map<String, String> attributes = new HashMap<>();
        attributeNames.forEach(item -> {
            attributes.put(item.toString(), genericAttributeService.getAttributeValue(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, item.toString()));
        });
        fillDefaultAttribute(attributes);
        return attributes;
    }

    @Override
    public void onCustomerDraftAttributeGreenFinshed(long customerId, SystemCustomerAttributeName attributeName, String value, SuggestionTypeEnum suggestionTypeEnum) {
        if (suggestionTypeEnum.equals(SuggestionTypeEnum.PASS)){
            genericAttributeService.addOrUpdateAttribute(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, attributeName.toString(), value);
        }
        genericAttributeService.deleteDraftAttribute(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, attributeName.toString());
    }

    public Customer getCustomer(long id, String userName, String phone, String email){
        LambdaQueryWrapper<Customer> customerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (id > 0){
            customerLambdaQueryWrapper.eq(Customer::getId, id);
        }
        if (!StringUtils.isEmpty(userName)){
            customerLambdaQueryWrapper.eq(Customer::getUserName, userName);
        }
        if (!StringUtils.isEmpty(phone)){
            customerLambdaQueryWrapper.eq(Customer::getPhone, phone);
        }
        if (!StringUtils.isEmpty(email)){
            customerLambdaQueryWrapper.eq(Customer::getEmail, email);
        }
        return customerMapper.selectOne(customerLambdaQueryWrapper);
    }

    public CustomerPassword getCustomerPassword(long id, long customerId){
        LambdaQueryWrapper<CustomerPassword> customerPasswordWrapper = new LambdaQueryWrapper<>();
        if (customerId > 0){
            customerPasswordWrapper.eq(CustomerPassword::getCustomerId, customerId);
        }
        return customerPasswordMapper.selectOne(customerPasswordWrapper);
    }

    @Override
    public Role getRole(String systemName) {
        return redisService.getCacheObject(redisKeyService.getRoleKey(systemName), () ->
                getRole(0, systemName));
    }

    public Role getRole(long id, String systemName){
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (id > 0){
            roleLambdaQueryWrapper.eq(Role::getId, id);
        }
        if (!StringUtils.isEmpty(systemName)){
            roleLambdaQueryWrapper.eq(Role::getSystemName, systemName);
        }
        return roleMapper.selectOne(roleLambdaQueryWrapper);
    }

    private Customer getCustomerByPhone(String phone){
        return getCustomer(0L, null, phone, null);
    }

    private void fillDefaultAttribute(Map<String, String> attribute){
        attribute.putIfAbsent(SystemCustomerAttributeName.NICK_NAME.name(), applicationConfig.getDefaultNickName());
        attribute.putIfAbsent(SystemCustomerAttributeName.AVATAR_ID.name(), applicationConfig.getDefaultAvatarId());
        attribute.putIfAbsent(SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID.name(), applicationConfig.getDefaultPersonalHomePageBackgroundId());
    }
}
