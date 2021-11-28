package com.x.provider.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.x.core.utils.ApiAssetUtil;
import com.x.provider.api.pay.model.ao.CreateBillAo;
import com.x.provider.pay.enums.PayResultCode;
import com.x.provider.pay.enums.bill.BillStatus;
import com.x.provider.pay.enums.bill.BillType;
import com.x.provider.pay.mapper.BillMapper;
import com.x.provider.pay.mapper.WalletMapper;
import com.x.provider.pay.model.domain.Bill;
import com.x.provider.pay.service.BillService;
import com.x.provider.pay.service.RedisKeyService;
import com.x.provider.pay.service.WalletService;
import com.x.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>账单的业务类</p>
 * <p>
 *     创建账单的要素:
 *     <li>1. 账单号</li>
 *     <li>2. 用户id</li>
 *     <li>3. 目标id, 无论是付款还是充值</li>
 *     <li>4. 账单产生原因 如充值等</li>
 *     <li>5. 账单金额</li>
 * </p>
 *
 */
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private RedisKeyService redisKeyService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BillMapper billMapper;
    @Autowired
    private WalletService walletService;
    @Autowired
    private WalletMapper walletMapper;

    @Override
    @Transactional
    public void insert(Bill bill) {
        billMapper.insert(bill);
    }

    /**
     * 创建一个订单, 订单信息都自己填写
     */
    @Override
    @Transactional
    public Bill createBill(CreateBillAo createBillAo) {
        Bill bill = Bill.builder()
                .serialNumber(IdWorker.getTimeId())
                .customerId(createBillAo.getCustomerId())
                .toCustomerId(createBillAo.getToCustomerId())
                .type(BillType.getEnum(createBillAo.getBillTypeCode()))
                .status(BillStatus.getEnum(createBillAo.getBillStatusCode()))
                .amount(createBillAo.getAmount())
                .comment(createBillAo.getComment())
                .build();
        insert(bill);  // 插入账单
        return bill;
    }

    /**
     * 订单发起者扣钱, 订单状态必须是PREPARED
     */
    @Override
    @Transactional
    public Bill launchBill(String billSerialNumber) {
        Bill bill = getBill(billSerialNumber);
        // 判断订单是不是合法的
        ApiAssetUtil.isTrue(bill.getStatus() == BillStatus.PREPARED, PayResultCode.BILL_IS_NOT_PREPARED);
        bill.setStatus(BillStatus.OPENING);
        // 判断是不是系统发放, 即customerId != 0
        if (bill.getCustomerId() != 0) {
           walletService.changeBalance(bill.getCustomerId(), bill.getAmount().negate());
        }
        updateBill(bill);
        return bill;
    }

    /**
     * 订单发起者扣钱, 订单状态必须是PREPARED
     */
    @Override
    @Transactional
    public Bill launchBill(Bill bill) {
        // 判断订单是不是合法的
        ApiAssetUtil.isTrue(bill.getStatus() == BillStatus.PREPARED, PayResultCode.BILL_IS_NOT_PREPARED);
        bill.setStatus(BillStatus.OPENING);
        // 判断是不是系统发放, 即customerId != 0
        if (bill.getCustomerId() != 0) {
            walletService.changeBalance(bill.getCustomerId(), bill.getAmount().negate());
        }
        updateBill(bill);
        return bill;
    }

    /**
     * 订单接收者加钱, 订单状态必须是OPENING
     */
    @Override
    @Transactional
    public Bill receiveBill(String billSerialNumber) {
        Bill bill = getBill(billSerialNumber);
        ApiAssetUtil.isTrue(bill.getStatus() == BillStatus.OPENING, PayResultCode.BILL_IS_NOT_OPENING);
        bill.setStatus(BillStatus.FINISHED);
        // 判断去向用户是不是空
        if (bill.getToCustomerId() != 0) {
            walletService.changeBalance(bill.getToCustomerId(), bill.getAmount());
        }
        updateBill(bill);
        return bill;
    }

    /**
     * 订单接收者加钱, 订单状态必须是OPENING
     */
    @Override
    @Transactional
    public Bill receiveBill(Bill bill) {
        ApiAssetUtil.isTrue(bill.getStatus() == BillStatus.OPENING, PayResultCode.BILL_IS_NOT_OPENING);
        bill.setStatus(BillStatus.FINISHED);
        // 判断去向用户是不是空
        if (bill.getToCustomerId() != 0) {
            walletService.changeBalance(bill.getToCustomerId(), bill.getAmount());
        }
        updateBill(bill);
        return bill;
    }

    /**
     * 精确获得订单
     * @param billId 唯一主键id
     * @param billSN 订单号
     * @return 订单
     */
    private Bill getAccurateBill(Long billId, String billSN) {
        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
        if (billId != null) {
            queryWrapper.eq(Bill::getId, billId);
        }
        if (billSN != null) {
            queryWrapper.eq(Bill::getSerialNumber, billSN);
        }
//        if (customerId != null) {
//            queryWrapper.eq(Bill::getCustomerId, customerId);
//        }
//        if (toCustomerId != null) {
//            queryWrapper.eq(Bill::getToCustomerId, toCustomerId);
//        }
//        if (amountLe != null) {
//            queryWrapper.le(Bill::getAmount, amountLe);
//        }
//        if (amountGe != null) {
//            queryWrapper.ge(Bill::getAmount, amountGe);
//        }
//        if (billStatus != null) {
//            queryWrapper.eq(Bill::getStatus, billStatus);
//        }
//        if (billType != null) {
//            queryWrapper.eq(Bill::getType, billType);
//        }
        return billMapper.selectOne(queryWrapper);
    }

    /**
     * 获得订单并缓存
     * @param billSN 订单号
     * @return 订单
     */
    @Override
    public Bill getBill(String billSN) {
        return redisService.getCacheObject(redisKeyService.getBillKey(billSN), () ->
                getAccurateBill(null, billSN));
    }

    /**
     * 更新订单, 并更新redis缓存
     * @param bill updateById的订单
     */
    @Override
    public void updateBill(Bill bill) {
        billMapper.updateById(bill);
        redisService.setCacheObject(redisKeyService.getBillKey(bill.getSerialNumber()), bill);
    }


}
