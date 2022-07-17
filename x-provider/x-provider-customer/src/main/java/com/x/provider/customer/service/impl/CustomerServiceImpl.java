package com.x.provider.customer.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.exception.ApiException;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.api.ResultCode;
import com.x.provider.api.customer.constants.CustomerEventTopic;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.api.customer.model.ao.ListSimpleCustomerAO;
import com.x.provider.api.customer.model.dto.CustomerAttributeDTO;
import com.x.provider.api.customer.model.dto.CustomerRelationDTO;
import com.x.provider.api.customer.model.dto.CustomerStatDTO;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import com.x.provider.api.customer.model.event.CustomerAttributeEvent;
import com.x.provider.api.customer.model.event.CustomerEvent;
import com.x.provider.api.customer.model.event.CustomerInfoGreenEvent;
import com.x.provider.api.mc.model.ao.SendVerificationCodeAO;
import com.x.provider.api.mc.model.ao.ValidateVerificationCodeAO;
import com.x.provider.api.mc.service.SmsRpcService;
import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import com.x.provider.api.oss.enums.SuggestionTypeEnum;
import com.x.provider.api.oss.model.ao.AttributeGreenRpcAO;
import com.x.provider.api.oss.model.ao.GreenRpcAO;
import com.x.provider.api.oss.service.GreenRpcService;
import com.x.provider.api.oss.service.OssRpcService;
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
import com.x.provider.customer.model.domain.*;
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
                               AuthenticationService authenticationService){
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
        sendCustomerInfoChanged(customer, null, CustomerEvent.EventTypeEnum.ADD);
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
        smsRpcService.validateVerificationCode(ValidateVerificationCodeAO.builder().phoneNumber(loginOrRegByPhoneAO.getPhoneNumber()).sms(loginOrRegByPhoneAO.getSmsVerificationCode()).build());
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
        sendCustomerInfoChanged(customer, null, CustomerEvent.EventTypeEnum.ADD);
        return customer;
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
        smsRpcService.sendVerificationCode(SendVerificationCodeAO.builder().phoneNumber(validatePhoneAO.getPhone()).build());
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
        smsRpcService.validateVerificationCode(ValidateVerificationCodeAO.builder().phoneNumber(bindPhoneAO.getPhone()).sms(bindPhoneAO.getSms()).build());
        customer.setPhone(bindPhoneAO.getPhone());
        customerMapper.updateById(customer);
        sendCustomerInfoChanged(customer, null, CustomerEvent.EventTypeEnum.ADD);
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
        smsRpcService.validateVerificationCode(ValidateVerificationCodeAO.builder().phoneNumber(changePhoneAO.getPhone()).sms(changePhoneAO.getSms()).build());
        if (!customer.getPhone().equals(changePhoneAO.getPhone())) {
            customer.setPhone(changePhoneAO.getPhone());
            Customer customerExisted = getCustomerByPhone(changePhoneAO.getPhone());
            ApiAssetUtil.isNull(customerExisted, UserResultCode.USER_PHONE_EXISTED);
            customerMapper.updateById(customer);
            sendCustomerInfoChanged(customer, null, CustomerEvent.EventTypeEnum.ADD);
        }
    }

    @Override
    public void changeUserName(long customerId, ChangeUserNameAO changeUserNameAO) {
        final Customer customer = getCustomer(customerId);
        Customer customerExisted = getCustomer(changeUserNameAO.getUserName());
        ApiAssetUtil.isNull(customerExisted, UserResultCode.USER_NAME_EXISTED);
        customer.setUserName(changeUserNameAO.getUserName());
        customerMapper.updateById(customer);
        sendCustomerInfoChanged(customer, null, CustomerEvent.EventTypeEnum.ADD);
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
    public void setCustomerDraftAttribute(long customerId, SystemCustomerAttributeName attributeName, String value) {
        switch (attributeName){
            case NICK_NAME:
            case SIGNATURE:
                R<String> greenResult = greenRpcService.greenSync(GreenRpcAO.builder().value(value).dataType(GreenDataTypeEnum.TEXT.name()).build());
                ApiAssetUtil.isTrue(greenResult.getData().equals(SuggestionTypeEnum.PASS.name()), ResultCode.GREEN_BLOCKED);
                genericAttributeService.addOrUpdateAttribute(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, attributeName.toString(), value);
                sendCustomerInfoChanged(Customer.builder().id(customerId).build(), Map.of(attributeName.name(), value), CustomerEvent.EventTypeEnum.UPDATE);
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
    public void onCustomerDraftAttributeGreenFinished(long customerId, SystemCustomerAttributeName attributeName, String value, SuggestionTypeEnum suggestionTypeEnum) {
        if (suggestionTypeEnum.equals(SuggestionTypeEnum.PASS)){
            genericAttributeService.addOrUpdateAttribute(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, attributeName.toString(), value);
            sendCustomerInfoChanged(Customer.builder().id(customerId).build(), Map.of(attributeName.name(), value), CustomerEvent.EventTypeEnum.UPDATE);
        }
        genericAttributeService.deleteDraftAttribute(AttributeKeyGroupEnum.CUSTOMER.toString(), customerId, attributeName.toString());
        kafkaTemplate.send(CustomerEventTopic.TOPIC_NAME_CUSTOMER_INFO_GREEN, String.valueOf(customerId), CustomerInfoGreenEvent.builder().customerId(customerId)
                .pass(suggestionTypeEnum.equals(SuggestionTypeEnum.PASS)).build());
    }

    @Override
    public Map<Long, SimpleCustomerDTO> listCustomer(ListSimpleCustomerAO listCustomerAO) {
        List<Customer> customers = customerMapper.selectList(buildQuery(0, listCustomerAO.getCustomerIds(), null, null, null));
        return prepare(listCustomerAO, customers);
    }

    public Customer getCustomer(long id, String userName, String phone, String email){
        LambdaQueryWrapper<Customer> customerLambdaQueryWrapper = buildQuery(id, null, userName, phone, email);
        return customerMapper.selectOne(customerLambdaQueryWrapper);
    }

    private LambdaQueryWrapper<Customer> buildQuery(long id, List<Long> idList, String userName, String phone, String email) {
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
        if (!CollectionUtils.isEmpty(idList)){
            customerLambdaQueryWrapper.in(Customer::getId, idList);
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

    private void fillDefaultAttribute(Map<String, String> attribute){
        attribute.putIfAbsent(SystemCustomerAttributeName.NICK_NAME.name(), applicationConfig.getDefaultNickName());
        attribute.putIfAbsent(SystemCustomerAttributeName.AVATAR_ID.name(), applicationConfig.getDefaultAvatarId());
        attribute.putIfAbsent(SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID.name(), applicationConfig.getDefaultPersonalHomePageBackgroundId());
    }

    private void sendCustomerInfoChanged(Customer customer, Map<String, String> customerAttribute, CustomerEvent.EventTypeEnum eventType){
        CustomerEvent customerEvent = BeanUtil.prepare(customer, CustomerEvent.class);
        Map<String, String> customerAttributeMap = listCustomerAttribute(customer.getId());
        CustomerAttributeEvent customerAttributeEvent = CustomerAttributeEvent.builder()
                .avatarId(customerAttributeMap.getOrDefault(SystemCustomerAttributeName.AVATAR_ID.name(), applicationConfig.getDefaultAvatarId()))
                .nickName(customerAttributeMap.getOrDefault(SystemCustomerAttributeName.NICK_NAME.name(), applicationConfig.getDefaultNickName()))
                .signature(customerAttributeMap.get(SystemCustomerAttributeName.SIGNATURE.name()))
                .personalHomePageBackgroundId(customerAttributeMap.getOrDefault(SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID.name(), applicationConfig.getDefaultPersonalHomePageBackgroundId()))
                .build();
        if (StringUtils.hasText(customerAttributeEvent.getAvatarId())){
            customerAttributeEvent.setAvatarUrl(ossRpcService.getObjectBrowseUrl(customerAttributeEvent.getAvatarId()).getData());
        }
        customerEvent.setEventType(eventType.getValue());
        customerEvent.setRegisterRole(true);
        customerEvent.setCustomerAttributeEvent(customerAttributeEvent);
        kafkaTemplate.send(CustomerEventTopic.TOPIC_NAME_CUSTOMER_INFO_CHANGED, String.valueOf(customer.getId()), customerEvent);
    }

    private Map<Long, SimpleCustomerDTO> prepare(ListSimpleCustomerAO listSimpleCustomerAO, List<Customer> source){
        if (source.isEmpty()){
            return Collections.emptyMap();
        }
        List<SimpleCustomerDTO> result = BeanUtil.prepare(source, SimpleCustomerDTO.class);
        prepareAttribute(result);
        if (listSimpleCustomerAO.getLoginCustomerId() > 0 && listSimpleCustomerAO.getCustomerOptions().contains(CustomerOptions.CUSTOMER_RELATION.name())) {
            prepareRelation(listSimpleCustomerAO.getLoginCustomerId(), CustomerRelationEnum.valueOf(listSimpleCustomerAO.getCustomerRelation()), result);
        }
        if (!CollectionUtils.isEmpty(listSimpleCustomerAO.getCustomerOptions()) && listSimpleCustomerAO.getCustomerOptions().contains(CustomerOptions.CUSTOMER_STAT.name())){
            prepareStat(result);
        }
        return result.stream().collect(Collectors.toMap(SimpleCustomerDTO::getId, item -> item));
    }

    private Map<Long, CustomerAttributeDTO> listCustomerAttributeMap(List<Long> customerIdList){
        Map<Long, Map<String, String>> customerAttribute = genericAttributeService.listAttributeMap(AttributeKeyGroupEnum.CUSTOMER.toString(), customerIdList);
        Map<Long, CustomerAttributeDTO> result = new HashMap<>(customerIdList.size());
        customerIdList.forEach(item ->{
            if (!customerAttribute.containsKey(item)){
                customerAttribute.put(item, new HashMap<>());
            }
            fillDefaultAttribute(customerAttribute.get(item));
            result.put(item, prepare(customerAttribute.get(item)));
        });
        fillUrl(result.values());
        return result;
    }

    private void prepareAttribute(List<SimpleCustomerDTO> source){
        List<Long> customerIdList = source.stream().map(SimpleCustomerDTO::getId).collect(Collectors.toList());
        Map<Long, CustomerAttributeDTO> customerAttributeMap = listCustomerAttributeMap(customerIdList);
        source.forEach(item -> {
            item.setAvatarUrl(customerAttributeMap.get(item.getId()).getAvatarUrl());
            item.setNickName(customerAttributeMap.get(item.getId()).getNickName());
        });
    }

    private void prepareRelation(long loginCustomerId, CustomerRelationEnum customerRelationEnum, List<SimpleCustomerDTO> source){
        if (loginCustomerId <= 0 || CustomerRelationEnum.NO_RELATION.getValue() == customerRelationEnum.getValue()){
            return;
        }
        List<Long> customerIdList = source.stream().map(SimpleCustomerDTO::getId).collect(Collectors.toList());
        Map<Long, CustomerRelation> customerRelations = customerRelationService.listRelationMap(loginCustomerId, customerIdList, customerRelationEnum);
        prepareRelation(loginCustomerId, source, customerRelations);
    }

    private void prepareStat(List<SimpleCustomerDTO> source){
        Map<Long, CustomerStat> customerStatMap = customerStatService.list(source.stream().map(SimpleCustomerDTO::getId).collect(Collectors.toList()));
        source.forEach(item -> {
            item.setStatistic(BeanUtil.prepare(customerStatMap.getOrDefault(item.getId(), CustomerStat.builder().id(item.getId()).build()), CustomerStatDTO.class));
        });
    }

    @Override
    public void prepareRelation(long loginCustomerId, List<SimpleCustomerDTO> source, Map<Long, CustomerRelation> customerRelations){
        if (loginCustomerId <= 0 || customerRelations.isEmpty()){
            return;
        }
        source.forEach(item ->{
            CustomerRelation customerRelation = customerRelations.get(item.getId());
            if (customerRelation == null){
                return;
            }
            item.setCustomerRelation(BeanUtil.prepare(customerRelation, CustomerRelationDTO.class));
            item.setCanFollow(!Objects.equals(loginCustomerId, item.getId()) && (customerRelation == null || !customerRelation.getFollow()));
        });
    }

    private CustomerAttributeDTO prepare(Map<String, String> attribute){
        attribute.putIfAbsent(SystemCustomerAttributeName.NICK_NAME.name(), applicationConfig.getDefaultNickName());
        attribute.putIfAbsent(SystemCustomerAttributeName.AVATAR_ID.name(), applicationConfig.getDefaultAvatarId());
        attribute.putIfAbsent(SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID.name(), applicationConfig.getDefaultPersonalHomePageBackgroundId());
        CustomerAttributeDTO result = CustomerAttributeDTO.builder()
                .nickName(attribute.getOrDefault(SystemCustomerAttributeName.NICK_NAME.name(), applicationConfig.getDefaultNickName()))
                .avatarId(attribute.getOrDefault(SystemCustomerAttributeName.AVATAR_ID.name(), applicationConfig.getDefaultAvatarId()))
                .personalHomePageBackgroundId(attribute.getOrDefault(SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID, applicationConfig.getDefaultPersonalHomePageBackgroundId()))
                .signature(attribute.getOrDefault(SystemCustomerAttributeName.SIGNATURE, ""))
                .build();
        return result;
    }

    private void fillUrl(Collection<CustomerAttributeDTO> source){
        if (source.isEmpty()){
            return;
        }
        Set<String> fileIds = new HashSet<>(source.size());
        fileIds.addAll(source.stream().filter(item -> !StringUtils.isEmpty(item.getAvatarId())).map(CustomerAttributeDTO::getAvatarId).collect(Collectors.toSet()));
        fileIds.addAll(source.stream().filter(item -> !StringUtils.isEmpty(item.getPersonalHomePageBackgroundId())).map(CustomerAttributeDTO::getPersonalHomePageBackgroundId).collect(Collectors.toSet()));
        Map<String, String> urls = ossRpcService.listObjectBrowseUrl(new ArrayList<>(fileIds)).getData();
        source.forEach(item ->{
            item.setAvatarUrl(urls.get(item.getAvatarId()));
            item.setPersonalHomePageBackgroundUrl(urls.get(item.getPersonalHomePageBackgroundId()));
        });
    }
}
