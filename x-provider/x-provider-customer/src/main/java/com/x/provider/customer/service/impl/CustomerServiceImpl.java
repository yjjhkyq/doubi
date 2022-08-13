package com.x.provider.customer.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.utils.CompareUtils;
import com.x.core.web.api.R;
import com.x.core.web.api.ResultCode;
import com.x.provider.api.customer.constants.CustomerEventTopic;
import com.x.provider.api.customer.model.event.CustomerAttributeEvent;
import com.x.provider.api.customer.model.event.CustomerEvent;
import com.x.provider.api.customer.model.event.CustomerInfoGreenEvent;
import com.x.provider.api.mc.model.dto.SendVerificationCodeDTO;
import com.x.provider.api.mc.model.dto.ValidateVerificationCodeDTO;
import com.x.provider.api.mc.service.SmsRpcService;
import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import com.x.core.domain.SuggestionTypeEnum;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenRequestDTO;
import com.x.provider.api.oss.model.dto.oss.GreenRequestDTO;
import com.x.provider.api.oss.service.GreenRpcService;
import com.x.provider.api.oss.service.OssRpcService;
import com.x.provider.customer.configure.ApplicationConfig;
import com.x.provider.customer.constant.CustomerConstants;
import com.x.provider.customer.enums.AttributeKeyGroupEnum;
import com.x.provider.customer.enums.SystemCustomerAttributeName;
import com.x.provider.customer.enums.SystemRoleNameEnum;
import com.x.provider.customer.enums.UserResultCode;
import com.x.provider.customer.mapper.*;
import com.x.provider.customer.model.ao.*;
import com.x.provider.customer.model.domain.*;
import com.x.provider.customer.model.query.CustomerAttributeQuery;
import com.x.provider.customer.model.query.CustomerQuery;
import com.x.provider.customer.service.*;
import com.x.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Map<String, String> DEFAULT_CUSTOMER_ATTRIBUTE = new HashMap<>();

    private final RedisKeyService redisKeyService;
    private final RedisService redisService;
    private final CustomerMapper customerMapper;
    private final CustomerPasswordMapper customerPasswordMapper;
    private final CustomerRoleMapper customerRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoderService passwordEncoderService;
    private final GenericAttributeService genericAttributeService;
    private final GreenRpcService greenRpcService;
    private final ApplicationConfig applicationConfig;
    private final SmsRpcService smsRpcService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OssRpcService ossRpcService;
    private final CustomerRelationService customerRelationService;
    private final CustomerStatService customerStatService;
    private final AuthenticationService authenticationService;
    private final CustomerAddressMapper customerAddressMapper;


    public CustomerServiceImpl(RedisKeyService redisKeyService,
                               RedisService redisService,
                               CustomerMapper customerMapper,
                               CustomerPasswordMapper customerPasswordMapper,
                               CustomerRoleMapper customerRoleMapper,
                               RoleMapper roleMapper,
                               PasswordEncoderService passwordEncoderService,
                               GenericAttributeService genericAttributeService,
                               GreenRpcService greenRpcService,
                               ApplicationConfig applicationConfig,
                               SmsRpcService smsRpcService,
                               KafkaTemplate<String, Object> kafkaTemplate,
                               OssRpcService ossRpcService,
                               CustomerRelationService customerRelationService,
                               CustomerStatService customerStatService,
                               AuthenticationService authenticationService,
                               CustomerAddressMapper customerAddressMapper){
        this.DEFAULT_CUSTOMER_ATTRIBUTE.putAll(Map.of(SystemCustomerAttributeName.NICK_NAME.name(), applicationConfig.getDefaultNickName(),
                SystemCustomerAttributeName.AVATAR_ID.name(), applicationConfig.getDefaultAvatarId(), SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID.name(),
                applicationConfig.getDefaultPersonalHomePageBackgroundId()));
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
        this.customerMapper =customerMapper;
        this.customerPasswordMapper = customerPasswordMapper;
        this.customerRoleMapper = customerRoleMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoderService = passwordEncoderService;
        this.genericAttributeService = genericAttributeService;
        this.greenRpcService = greenRpcService;
        this.applicationConfig = applicationConfig;
        this.smsRpcService = smsRpcService;
        this.kafkaTemplate = kafkaTemplate;
        this.ossRpcService = ossRpcService;
        this.customerRelationService = customerRelationService;
        this.customerStatService = customerStatService;
        this.authenticationService = authenticationService;
        this.customerAddressMapper = customerAddressMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserNamePasswordRegisterAO userNamePasswordRegisterAO) {
        Customer customerExisted = getCustomer(userNamePasswordRegisterAO.getUserName());
        ApiAssetUtil.isNull(customerExisted, UserResultCode.USER_NAME_EXISTED);
        Customer customer = new Customer(userNamePasswordRegisterAO.getUserName());
        registerCustomer(customer);
        CustomerPassword customerPassword = new CustomerPassword(customer.getId(), RandomUtil.randomNumbers(4));
        customerPassword.setPassword(passwordEncoderService.encode(userNamePasswordRegisterAO.getPassword(), customerPassword.getPasswordSalt()));
        customerPasswordMapper.insert(customerPassword);
        sendCustomerInfoChanged(customer, CustomerEvent.EventTypeEnum.ADD);
    }

    @Override
    public String loginByPassword(LoginByPasswordAO userNamePasswordLoginAO) {
        Customer customer = getCustomer(0L, userNamePasswordLoginAO.getUserName(), userNamePasswordLoginAO.getPhone(), null);
        ApiAssetUtil.notNull(customer, UserResultCode.USER_NAME_OR_PWD_ERROR);
        ApiAssetUtil.isTrue(customer.isActive(), UserResultCode.CUSTOMER_NOT_ACTIVE);
        CustomerPassword customerPassword = getCustomerPassword(customer.getId());
        ApiAssetUtil.isTrue(passwordEncoderService.matches(userNamePasswordLoginAO.getPassword(), customerPassword.getPasswordSalt(), customerPassword.getPassword()), UserResultCode.USER_NAME_OR_PWD_ERROR);
        String token = authenticationService.signIn(customer);
        return token;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String loginOrRegisterBySms(LoginOrRegBySmsAO loginOrRegByPhoneAO) {
        smsRpcService.validateVerificationCode(ValidateVerificationCodeDTO.builder().phoneNumber(loginOrRegByPhoneAO.getPhoneNumber()).sms(loginOrRegByPhoneAO.getSmsVerificationCode()).build());
        Customer customer = getCustomerByPhone(loginOrRegByPhoneAO.getPhoneNumber());
        if (customer == null){
            customer = Customer.builder().phone(loginOrRegByPhoneAO.getPhoneNumber()).build();
            registerCustomer(customer);
        }
        String token = authenticationService.signIn(customer);
        return token;
    }

    @Override
    public Customer registerCustomer(Customer customer){
        customerMapper.insert(customer);
        Role registeredRole = getRole(SystemRoleNameEnum.REGISTERED.name());
        CustomerRole customerRole = new CustomerRole(customer.getId(), registeredRole.getId());
        customerRoleMapper.insert(customerRole);
        sendCustomerInfoChanged(customer, CustomerEvent.EventTypeEnum.ADD);
        return customer;
    }

    @Override
    public List<Customer> listCustomer(List<Long> idList) {
        return customerMapper.selectList(buildQuery(CustomerQuery.builder().idList(idList).build()));
    }

    /**
     * 验证用户待绑定的手机号是否合法
     * <li>1. 验证这个用户有没有绑定手机(仅保险起见)</li>
     * <li>2. 验证有没有其他用户用这个手机号绑定</li>
     * <li>3. 给这个手机号发验证码</li>
     * @param customerId 用户id
     * @param validatePhoneAO 待绑定的手机号
     */
    @Override
    public void checkPhoneBound(long customerId, ValidatePhoneAO validatePhoneAO) {
        Customer customerExisted = getCustomer(customerId);
        // 判断用户是不是没绑定手机号
        ApiAssetUtil.isStringEmpty(customerExisted.getPhone(), UserResultCode.USER_PHONE_BOUND);
        // 用户未绑定手机号
        customerExisted = getCustomer(0L, null, validatePhoneAO.getPhone(), null);
        // 判断是否是未被绑定的
        ApiAssetUtil.isNull(customerExisted, UserResultCode.USER_PHONE_BOUND);
        // 发送验证码
        smsRpcService.sendVerificationCode(SendVerificationCodeDTO.builder().phoneNumber(validatePhoneAO.getPhone()).build());
    }

    /**
     * 用户绑定手机, 必须调用子接口validate, 验证手机号是否已被绑定
     * <li>1. 验证验证码正确与否</li>
     * <li>2. 插入数据库</li>
     * @param customerId 用户id
     * @param bindPhoneAO 传入待绑定号码和短信验证码
     */
    @Override
    public void bindPhone(long customerId, BindPhoneAO bindPhoneAO) {
        // 用户必须是没有绑定手机号的
        Customer customer = getCustomer(customerId);
        smsRpcService.validateVerificationCode(ValidateVerificationCodeDTO.builder().phoneNumber(bindPhoneAO.getPhone()).sms(bindPhoneAO.getSms()).build());
        customer.setPhone(bindPhoneAO.getPhone());
        customerMapper.updateById(customer);
        sendCustomerInfoChanged(customer, CustomerEvent.EventTypeEnum.ADD);
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
    }

    @Override
    public void changePhone(long customerId, ChangePhoneAO changePhoneAO) {
        final Customer customer = getCustomer(customerId, null, changePhoneAO.getOldPhone(), null);
        ApiAssetUtil.notNull(customer, UserResultCode.USER_PHONE_ERROR);
        // 验证码校验可以放在if里也可以放在if外, 放在if里效率高, 放在if外不会有未被利用的验证码
        smsRpcService.validateVerificationCode(ValidateVerificationCodeDTO.builder().phoneNumber(changePhoneAO.getPhone()).sms(changePhoneAO.getSms()).build());
        if (!customer.getPhone().equals(changePhoneAO.getPhone())) {
            customer.setPhone(changePhoneAO.getPhone());
            Customer customerExisted = getCustomerByPhone(changePhoneAO.getPhone());
            ApiAssetUtil.isNull(customerExisted, UserResultCode.USER_PHONE_EXISTED);
            customerMapper.updateById(customer);
            sendCustomerInfoChanged(customer, CustomerEvent.EventTypeEnum.ADD);
        }
    }

    @Override
    public void changeUserName(long customerId, ChangeUserNameAO changeUserNameAO) {
        final Customer customer = getCustomer(customerId);
        Customer customerExisted = getCustomer(changeUserNameAO.getUserName());
        ApiAssetUtil.isNull(customerExisted, UserResultCode.USER_NAME_EXISTED);
        customer.setUserName(changeUserNameAO.getUserName());
        customerMapper.updateById(customer);
        sendCustomerInfoChanged(customer, CustomerEvent.EventTypeEnum.ADD);
    }

    @Override
    public Customer getCustomer(String userName) {
        return getCustomer(0, userName, null, null);
    }

    @Override
    public Customer getCustomer(long id) {
        return getCustomer(id, null, null, null);
    }

    @Override
    public CustomerPassword getCustomerPassword(long customerId) {
        return getCustomerPassword(0, customerId);
    }

    @Override
    public List<Role> listCustomerRole(long customerId) {
        List<CustomerRole> customerRoles = customerRoleMapper.selectList(new LambdaQueryWrapper<CustomerRole>().eq(CustomerRole::getCustomerId, customerId));
        if (customerRoles.isEmpty()){
            return Collections.emptyList();
        }
        return roleMapper.selectBatchIds(customerRoles.stream().map(CustomerRole::getRoleId).collect(Collectors.toList()));
    }

    @Override
    public void setCustomerAttribute(long customerId, SystemCustomerAttributeName attributeName, String value) {
        switch (attributeName.getGreenDataType()){
            case NULL:
                genericAttributeService.addOrUpdateAttribute(AddOrUpdateAttributeAO.builder()
                        .keyGroup(AttributeKeyGroupEnum.CUSTOMER.name())
                        .key(attributeName.toString())
                        .entityId(customerId)
                        .suggestionType(SuggestionTypeEnum.PASS.getValue())
                        .value(value)
                        .build());
            case TEXT:
                R<String> greenResult = greenRpcService.greenSync(GreenRequestDTO.builder().value(value).dataType(GreenDataTypeEnum.TEXT.name()).build());
                ApiAssetUtil.isTrue(greenResult.getData().equals(SuggestionTypeEnum.PASS.name()), ResultCode.GREEN_BLOCKED);
                genericAttributeService.addOrUpdateAttribute(AddOrUpdateAttributeAO.builder()
                        .keyGroup(AttributeKeyGroupEnum.CUSTOMER.name())
                        .key(attributeName.toString())
                        .entityId(customerId)
                        .suggestionType(SuggestionTypeEnum.PASS.getValue())
                        .value(value)
                        .build());
                sendCustomerInfoChanged(Customer.builder().id(customerId).build(), CustomerEvent.EventTypeEnum.UPDATE);
                break;
            case PICTURE:
                setCustomerMediaAttribute(customerId, attributeName, value);
                break;
        }

        switch (attributeName){
            case NICK_NAME:
            case SIGNATURE:
                sendCustomerInfoChanged(Customer.builder().id(customerId).build(), CustomerEvent.EventTypeEnum.UPDATE);
                break;
        }
    }

    private void setCustomerMediaAttribute(long customerId, SystemCustomerAttributeName attributeName, String value) {
        genericAttributeService.addOrUpdateDraftAttribute(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, attributeName.toString(), value);
        try {
            greenRpcService.greenAttributeAsync(new AttributeGreenRequestDTO(redisKeyService.getGlobalAttributeKeyGroup(AttributeKeyGroupEnum.CUSTOMER.toString()),
                    customerId, attributeName.toString(), value, GreenDataTypeEnum.PICTURE, CustomerConstants.CUSTOMER_ATTRIBUTE_GREEN_CALLBACK_RUL));
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
            genericAttributeService.deleteDraftAttribute(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, attributeName.toString());
            throw e;
        }
    }

    @Override
    public Map<Long, List<GenericAttribute>> listCustomerAttribute(CustomerAttributeQuery customerAttributeQuery) {
        return genericAttributeService.listAttributeMap(AttributeKeyGroupEnum.CUSTOMER.toString(), customerAttributeQuery.getCustomerIdList()).stream().filter(item -> (
            customerAttributeQuery.getSuggestionType() == null || customerAttributeQuery.getSuggestionType().getValue().equals(item.getSuggestionType())))
                .collect(Collectors.groupingBy(item -> item.getEntityId()));
    }

    @Override
    public Map<Long, List<GenericAttribute>> listAndFillDefaultCustomerAttribute(CustomerAttributeQuery customerAttributeQuery) {
        final Map<Long, List<GenericAttribute>> result = listCustomerAttribute(customerAttributeQuery);
        customerAttributeQuery.getCustomerIdList().stream().filter(item -> !result.keySet().contains(item)).forEach(item -> {
            result.put(item, new ArrayList<>());
        });
        result.entrySet().forEach(item -> {
            final Set<String> attributeKeyPassed = item.getValue().stream().filter(opt -> opt.getSuggestionType().equals(SuggestionTypeEnum.PASS.getValue()))
                    .map(GenericAttribute::getKey).collect(Collectors.toSet());
            DEFAULT_CUSTOMER_ATTRIBUTE.entrySet().forEach(opt -> {
                if (attributeKeyPassed.contains(opt.getKey())){
                    return;
                }
                item.getValue().add(GenericAttribute.builder().keyGroup(AttributeKeyGroupEnum.CUSTOMER.name()).key(opt.getKey()).value(opt.getValue()).entityId(item.getKey())
                        .id(0L).suggestionType(SuggestionTypeEnum.PASS.getValue()).build());
            });
        });
        return result;
    }

    @Override
    public void onCustomerDraftAttributeGreenFinished(long customerId, SystemCustomerAttributeName attributeName, String value, SuggestionTypeEnum suggestionTypeEnum) {
        if (suggestionTypeEnum.equals(SuggestionTypeEnum.PASS)){
            final GenericAttribute genericAttribute = genericAttributeService.addOrUpdateAttribute(AddOrUpdateAttributeAO.builder()
                    .keyGroup(AttributeKeyGroupEnum.CUSTOMER.name())
                    .entityId(customerId)
                    .key(attributeName.name())
                    .value(value)
                    .suggestionType(SuggestionTypeEnum.PASS.getValue())
                    .build());
            sendCustomerInfoChanged(Customer.builder().id(customerId).build(), CustomerEvent.EventTypeEnum.UPDATE);
        }
        genericAttributeService.deleteDraftAttribute(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, attributeName.toString());
        kafkaTemplate.send(CustomerEventTopic.TOPIC_NAME_CUSTOMER_INFO_GREEN, String.valueOf(customerId), CustomerInfoGreenEvent.builder().customerId(customerId)
                .pass(suggestionTypeEnum.equals(SuggestionTypeEnum.PASS)).build());
    }

    public Customer getCustomer(long id, String userName, String phone, String email){
        CustomerQuery query = CustomerQuery.builder().id(id).userName(userName).phone(phone).email(email).build();
        LambdaQueryWrapper<Customer> customerLambdaQueryWrapper = buildQuery(query);
        return customerMapper.selectOne(customerLambdaQueryWrapper);
    }

    public Map<Long, Customer> listCustomer(CustomerQuery customerQuery){
        return customerMapper.selectList(buildQuery(customerQuery)).stream().collect(Collectors.toMap(item -> item.getId(), item -> item));
    }

    private LambdaQueryWrapper<Customer> buildQuery(CustomerQuery customerQuery) {
        LambdaQueryWrapper<Customer> customerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (CompareUtils.gtZero(customerQuery.getId())){
            customerLambdaQueryWrapper.eq(Customer::getId, customerQuery.getId());
        }
        if (!StringUtils.isEmpty(customerQuery.getUserName())){
            customerLambdaQueryWrapper.eq(Customer::getUserName, customerQuery.getUserName());
        }
        if (!StringUtils.isEmpty(customerQuery.getPhone())){
            customerLambdaQueryWrapper.eq(Customer::getPhone, customerQuery.getPhone());
        }
        if (!StringUtils.isEmpty(customerQuery.getEmail())){
            customerLambdaQueryWrapper.eq(Customer::getEmail, customerQuery.getEmail());
        }
        if (!CollectionUtils.isEmpty(customerQuery.getIdList())){
            customerLambdaQueryWrapper.in(Customer::getId, customerQuery.getIdList());
        }
        return customerLambdaQueryWrapper;
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
        return getRole(0, systemName);
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

    private void sendCustomerInfoChanged(Customer customer, CustomerEvent.EventTypeEnum eventType){
        CustomerEvent customerEvent = BeanUtil.prepare(customer, CustomerEvent.class);
        final Map<String, String> customerAttribute = listCustomerAttribute(CustomerAttributeQuery.builder()
                .suggestionType(SuggestionTypeEnum.PASS)
                .customerIdList(Arrays.asList(customer.getId())).build()).get(customer.getId()).stream().collect(Collectors.toMap(item -> item.getKey(), item -> item.getValue()));
        CustomerAttributeEvent customerAttributeEvent = CustomerAttributeEvent.builder()
                .avatarId(customerAttribute.getOrDefault(SystemCustomerAttributeName.AVATAR_ID.name(), applicationConfig.getDefaultAvatarId()))
                .nickName(customerAttribute.getOrDefault(SystemCustomerAttributeName.NICK_NAME.name(), applicationConfig.getDefaultNickName()))
                .signature(customerAttribute.get(SystemCustomerAttributeName.SIGNATURE.name()))
                .personalHomePageBackgroundId(customerAttribute.getOrDefault(SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID.name(), applicationConfig.getDefaultPersonalHomePageBackgroundId()))
                .build();
        customerEvent.setEventType(eventType.getValue());
        customerEvent.setRegisterRole(true);
        customerEvent.setCustomerAttributeEvent(customerAttributeEvent);
        kafkaTemplate.send(CustomerEventTopic.TOPIC_NAME_CUSTOMER_INFO_CHANGED, String.valueOf(customer.getId()), customerEvent);
    }
}
