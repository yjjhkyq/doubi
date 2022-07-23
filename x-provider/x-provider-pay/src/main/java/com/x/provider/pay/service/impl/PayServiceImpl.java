package com.x.provider.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.provider.api.pay.constant.PayEventTopic;
import com.x.provider.api.pay.enums.PayErrorEnum;
import com.x.provider.api.pay.enums.PaymentStatusEnum;
import com.x.provider.api.pay.model.ao.TransactionAo;
import com.x.provider.api.pay.model.dto.TransactionDTO;
import com.x.provider.api.pay.model.event.TransactionEvent;
import com.x.provider.pay.mapper.AssetMapper;
import com.x.provider.pay.mapper.OrderItemMapper;
import com.x.provider.pay.mapper.OrderMapper;
import com.x.provider.pay.mapper.TransactionMapper;
import com.x.provider.pay.model.ao.IncAssetAO;
import com.x.provider.pay.model.domain.Asset;
import com.x.provider.pay.model.domain.Order;
import com.x.provider.pay.model.domain.OrderItem;
import com.x.provider.pay.model.domain.Transaction;
import com.x.provider.pay.model.query.AssetQuery;
import com.x.provider.pay.service.PayService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PayServiceImpl implements PayService {

    private final AssetMapper assetMapper;
    private final TransactionMapper transactionMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PayServiceImpl(AssetMapper assetMapper,
                          TransactionMapper transactionMapper,
                          OrderMapper orderMapper,
                          OrderItemMapper orderItemMapper,
                          KafkaTemplate<String, Object> kafkaTemplate){
        this.assetMapper = assetMapper;
        this.transactionMapper = transactionMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransactionDTO transaction(TransactionAo transaction) {
        List<IncAssetAO> incAssetList = prepare(transaction);
        incAsset(incAssetList);
        Transaction transactionEntity = BeanUtil.prepare(transaction, Transaction.class);
        if (transaction.getOrder() != null){
            Order order = BeanUtil.prepare(transaction.getOrder(), Order.class);
            order.setPaymentStatus(PaymentStatusEnum.PAID.getValue());
            order.setPaidDate(new Date());
            orderMapper.insert(order);
            transactionEntity.setOrderId(order.getId());
            if (!CollectionUtils.isEmpty(transaction.getOrder().getOrderItemList())){
                List<OrderItem> orderItemList = BeanUtil.prepare(transaction.getOrder().getOrderItemList(), OrderItem.class);
                orderItemList.forEach(item ->{
                    item.setOrderId(order.getId());
                    orderItemMapper.insert(item);
                });
            }
        }
        transactionMapper.insert(transactionEntity);
        kafkaTemplate.send(PayEventTopic.TOPIC_NAME_TRANSACTION, BeanUtil.prepare(transaction, TransactionEvent.class));
        return TransactionDTO.builder().id(transactionEntity.getId()).build();
    }


    @Transactional(rollbackFor = Exception.class)
    public void incAsset(List<IncAssetAO> incAssetList){
        ApiAssetUtil.isTrue(canIncAsset(incAssetList), PayErrorEnum.ASSET_NOT_ENOUGHT);
        for (IncAssetAO incAsset: incAssetList) {
            Integer result = assetMapper.incAsset(incAsset);
            ApiAssetUtil.isTrue(result > 0, PayErrorEnum.ASSET_NOT_ENOUGHT);
        }
    }

    @Override
    public Asset getAsset(AssetQuery assetQuery){
        LambdaQueryWrapper query = buildAssetQuery(assetQuery);
        return assetMapper.selectOne(query);
    }

    @Override
    public void initAsset(Long customerId) {
        Asset asset = getAsset(AssetQuery.builder().customerId(customerId).build());
        if (asset != null){
            return;
        }
        assetMapper.insert(Asset.builder().customerId(customerId).build());
    }

    private List<IncAssetAO> prepare(TransactionAo makeTransactionAo){
        List<IncAssetAO> result = new ArrayList<>(2);
        if (makeTransactionAo.getFromCustomerId() > 0){
            result.add(IncAssetAO.builder()
                    .customerId(makeTransactionAo.getFromCustomerId())
                    .coin(makeTransactionAo.getFromCoin())
                    .rice(makeTransactionAo.getFromRice())
                    .costCoin(makeTransactionAo.getFromCostCoin())
                    .build());
        }
        if (makeTransactionAo.getToCustomerId() > 0){
            result.add(IncAssetAO.builder()
                    .customerId(makeTransactionAo.getToCustomerId())
                    .coin(makeTransactionAo.getToCoin())
                    .rice(makeTransactionAo.getToRice())
                    .costCoin(makeTransactionAo.getToCostCoin())
                    .build());
        }
        return result;
    }

    private boolean canIncAsset(List<IncAssetAO> incAssetList){
        for (IncAssetAO incAsset: incAssetList) {
            if (!canIncAsset(incAsset)){
                return false;
            }
        }
        return true;
    }

    private boolean canIncAsset(IncAssetAO assetTransaction){
        if (assetTransaction.getCoin() >= 0 && assetTransaction.getRice() >=0){
            return true;
        }
        Asset fromAsset = getAsset(AssetQuery.builder().customerId(assetTransaction.getCustomerId()).build());
        return fromAsset.getCoin() + assetTransaction.getCoin() >= 0 && fromAsset.getRice() + assetTransaction.getRice() >= 0;
    }

    private LambdaQueryWrapper buildAssetQuery(AssetQuery assetQuery){
        LambdaQueryWrapper<Asset> query = new LambdaQueryWrapper<>();
        if (assetQuery.getCustomerId() != null){
            query = query.eq(Asset::getCustomerId, assetQuery.getCustomerId());
        }
        return query;
    }
}
