package com.x.provider.pay.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.exception.ApiException;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.IdUtils;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.pay.model.ao.CreateWalletAO;
import com.x.provider.pay.enums.PayResultCode;
import com.x.provider.pay.mapper.WalletMapper;
import com.x.provider.pay.mapper.WalletPasswordMapper;
import com.x.provider.pay.model.ao.ValidateWalletPasswordAO;
import com.x.provider.pay.model.domain.Wallet;
import com.x.provider.pay.model.domain.WalletPassword;
import com.x.provider.pay.service.PasswordEncoderService;
import com.x.provider.pay.service.RedisKeyService;
import com.x.provider.pay.service.WalletService;
import com.x.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;

@Service
public class WalletServiceImpl implements WalletService {

    private final Duration TOKEN_EXPIRED_DURATION = Duration.ofMinutes(5);
    private final Duration VALIDATE_TIMES_EXPIRED_DURATION = Duration.ofHours(1);

    private final static BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(0);
    private final static Integer MAX_VALIDATE_TIME = 5;

    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private CustomerRpcService customerRpcService;
    @Autowired
    private PasswordEncoderService passwordEncoderService;
    @Autowired
    private WalletPasswordMapper walletPasswordMapper;
    @Autowired
    private RedisKeyService redisKeyService;
    @Autowired
    private RedisService redisService;

    /**
     * 创建钱包简单逻辑
     * <p>根据用户是否有手机号来判断钱包是否激活</p>
     * <p>只有当用户通过手机注册, 才会发生传手机号的情况</p>
     * <li>1. 当用户未绑定手机号 不会active</li>
     * <li>2. 当用户已存在钱包, 报错</li>
     * <li>3. 当用户传入手机号与字段不一样情况下, 会active = false</li>
     *
     * @param createWalletAO 接收用户id 用户手机号 密码
     */
    @Override
    @Transactional
    public void createWallet(CreateWalletAO createWalletAO) {
        // 1. 验证是否已创建
        Wallet walletExisted = getWallet(createWalletAO.getCustomerId());
        ApiAssetUtil.isNull(walletExisted, PayResultCode.USER_WALLET_CREATED);
        // 2. 验证是否有手机号
        CustomerDTO customer = customerRpcService.getCustomer(createWalletAO.getCustomerId(), Arrays.asList(CustomerOptions.CUSTOMER.name())).getData();
        Wallet wallet = Wallet.builder().customerId(createWalletAO.getCustomerId()).balance(DEFAULT_BALANCE).build();
        if (!StringUtils.isEmpty(customer.getPhone())) {
            // 有手机的情况
            // 3. 在进行手机号码与传入的手机号码的二次校验
            wallet.setActive(customer.getPhone().equals(createWalletAO.getPhone()));
        }
        walletMapper.insert(wallet);
        // 创建密码
        String salt = RandomUtil.randomNumbers(4);
        // TODO: 是否需要校验密码规则, 即6位阿拉伯数字
        WalletPassword walletPassword = WalletPassword.builder()
                .walletId(wallet.getId())
                .passwordSalt(salt)
                .password(passwordEncoderService.encode(createWalletAO.getWalletPassword(), salt))
                .build();
        walletPasswordMapper.insert(walletPassword);
    }


    /**
     * 根据用户id获得钱包情况
     *
     * @param customerId 用户id
     * @return 钱包实体类
     */
    @Override
    public Wallet getWallet(long customerId) {
        return redisService.getCacheObject(redisKeyService.getWalletKey(customerId), () ->
                getWallet(null, customerId, null, null));
    }

    /**
     * interceptor判断用户支付token
     * <p>流程:</p>
     * <li>1. 先拿到用户id</li>
     * <li>2. 判断redis中的id是不是和这个id一样</li>
     * <li>3. 给redis续约?</li>
     * @param token 前端传入的token
     * @param customerId
     * @return 成功与否
     */
    @Override
    public boolean validateWalletToken(String token, long customerId) {
        Long cacheCustomerId = redisService.getLongCacheObject(redisKeyService.getWalletPasswordInfoKey(token));
        return customerId == cacheCustomerId;
    }

    /**
     * 验证支付密码的逻辑
     * <li>1. 判断有没有超过错误次数, 次数记录在redis中, key为redisKeyService.getWalletPasswordValidateTimesKey(customerId)</li>
     * <li>2. 拿到用户钱包判断钱包状态, 是否激活, 是否冻结</li>
     * <li>3. 拿到用户钱包密码, 判断密码正确与否</li>
     * <li>4. 如果错误, 计数器加一, 抛出密码错误异常</li>
     * <li>5. 如果成功, 生成token, 计数器清零, 缓存redis登录状态, 键为redisKeyService.getWalletPasswordInfoKey(token), 值为customerId</li>
     * @param customerId 用户id
     * @param validateWalletPasswordAO 支付密码
     * @return 成功后的token
     */
    @Override
    public String validateWalletPassword(long customerId, ValidateWalletPasswordAO validateWalletPasswordAO) {
        // 判断有没有有没有超过错误次数
        ApiAssetUtil.isTrue(MAX_VALIDATE_TIME >= redisService.getCountObject(redisKeyService.getWalletPasswordValidateTimesKey(customerId)), PayResultCode.WALLET_PASSWORD_VALIDATED_LOCKED);

        Wallet wallet = getWallet(customerId);
        // 判断钱包已激活
        ApiAssetUtil.isTrue(wallet.isActive(), PayResultCode.USER_WALLET_NON_ACTIVE);
        // 判断钱包未锁定
        ApiAssetUtil.notTrue(wallet.isLocked(), PayResultCode.USER_WALLET_LOCKED);
        // 获得钱包支付密码
        WalletPassword walletPassword = getWalletPassword(customerId);
        // 这里没有直接使用断言是因为我要记录失败次数
        // ApiAssetUtil.isTrue(passwordEncoderService.matches(validateWalletPasswordAO.getPassword(), walletPassword.getPasswordSalt(), walletPassword.getPassword()), PayResultCode.WALLET_PASSWORD_ERROR);

        if (!passwordEncoderService.matches(validateWalletPasswordAO.getPassword(), walletPassword.getPasswordSalt(), walletPassword.getPassword())) {
            redisService.setCountObject(redisKeyService.getWalletPasswordValidateTimesKey(customerId), VALIDATE_TIMES_EXPIRED_DURATION);
            throw new ApiException(PayResultCode.WALLET_PASSWORD_ERROR);
        }
        // 清除错误次数
        redisService.deleteObject(redisKeyService.getWalletPasswordValidateTimesKey(customerId));
        String token = IdUtils.fastSimpleUUID();
        redisService.setCacheObject(redisKeyService.getWalletPasswordInfoKey(token), Long.valueOf(customerId), TOKEN_EXPIRED_DURATION);
        return token;
    }

    /**
     * 根据条件筛选钱包情况, 不需要的条件传null
     *
     * @param id         钱包id
     * @param customerId 所属用户id
     * @param active     是否激活状态
     * @param locked     是否锁定状态
     * @return 返回第一个钱包结果
     */
    private Wallet getWallet(Long id, Long customerId, Boolean active, Boolean locked) {
        LambdaQueryWrapper<Wallet> queryWrapper = new LambdaQueryWrapper<>();
        if (id != null && id > 0) {
            queryWrapper.eq(Wallet::getId, id);
        }
        if (customerId != null && customerId > 0) {
            queryWrapper.eq(Wallet::getCustomerId, customerId);
        }
        if (active != null) {
            queryWrapper.eq(Wallet::isActive, active);
        }
        if (locked != null) {
            queryWrapper.eq(Wallet::isLocked, locked);
        }
        return walletMapper.selectOne(queryWrapper);
    }

    private WalletPassword getWalletPassword(Long id, Long walletId, Long customerId) {
        LambdaQueryWrapper<WalletPassword> queryWrapper = new LambdaQueryWrapper<>();
        if (id != null && id > 0) {
            queryWrapper.eq(WalletPassword::getId, id);
        }
        if (walletId != null && walletId > 0) {
            queryWrapper.eq(WalletPassword::getWalletId, walletId);
        }
        if (customerId != null && customerId > 0) {
            queryWrapper.eq(WalletPassword::getCustomerId, customerId);
        }
        return walletPasswordMapper.selectOne(queryWrapper);
    }

    private WalletPassword getWalletPassword(long customerId) {
        return redisService.getCacheObject(redisKeyService.getWalletPasswordKey(customerId), () ->
                getWalletPassword(null, null, customerId));
    }
}
