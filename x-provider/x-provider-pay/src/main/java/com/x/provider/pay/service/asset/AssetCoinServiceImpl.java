package com.x.provider.pay.service.asset;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.provider.api.pay.constant.PayEventTopic;
import com.x.provider.api.pay.enums.PayErrorEnum;
import com.x.provider.api.pay.model.dto.CreateTransactionDTO;
import com.x.provider.api.pay.model.event.TransactionEvent;
import com.x.provider.pay.mapper.AssetCoinMapper;
import com.x.provider.pay.mapper.TransactionMapper;
import com.x.provider.pay.model.bo.asset.IncAssetCoinBO;
import com.x.provider.pay.model.domain.asset.AssetCoin;
import com.x.provider.pay.model.domain.asset.Transaction;
import com.x.provider.pay.model.query.asset.AssetCoinQuery;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetCoinServiceImpl implements AssetCoinService {

    private final AssetCoinMapper assetCoinMapper;
    private final TransactionMapper transactionMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AssetCoinServiceImpl(AssetCoinMapper assetCoinMapper,
                                TransactionMapper transactionMapper,
                                KafkaTemplate<String, Object> kafkaTemplate){
        this.assetCoinMapper = assetCoinMapper;
        this.transactionMapper = transactionMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incAsset(List<IncAssetCoinBO> incAssetList){
        ApiAssetUtil.isTrue(canIncAsset(incAssetList), PayErrorEnum.ASSET_NOT_ENOUGHT);
        for (IncAssetCoinBO incAsset: incAssetList) {
            initAsset(incAsset.getCustomerId());
            Integer result = assetCoinMapper.incAsset(incAsset);
            ApiAssetUtil.isTrue(result > 0, PayErrorEnum.ASSET_NOT_ENOUGHT);
        }
    }

    @Override
    public AssetCoin getAsset(Long customerId){
        return getAsset(AssetCoinQuery.builder().customerId(customerId).build());
    }

    public AssetCoin getAsset(AssetCoinQuery assetQuery){
        LambdaQueryWrapper query = buildAssetQuery(assetQuery);
        return assetCoinMapper.selectOne(query);
    }

    @Override
    public void initAsset(Long customerId) {
        AssetCoin asset = getAsset(AssetCoinQuery.builder().customerId(customerId).build());
        if (asset != null){
            return;
        }
        assetCoinMapper.insert(AssetCoin.builder().customerId(customerId).build());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long transaction(CreateTransactionDTO transaction) {

        Transaction transactionEntity = BeanUtil.prepare(transaction, Transaction.class);
        transactionEntity.setOrderId(transaction.getOrderId());
        List<IncAssetCoinBO> incAssetList = prepare(transaction);
        incAsset(incAssetList);
        transactionMapper.insert(transactionEntity);
        kafkaTemplate.send(PayEventTopic.TOPIC_NAME_TRANSACTION, BeanUtil.prepare(transaction, TransactionEvent.class));
        return transactionEntity.getId();

    }


    private List<IncAssetCoinBO> prepare(CreateTransactionDTO makeTransactionAo){
        List<IncAssetCoinBO> result = new ArrayList<>(2);
        if (makeTransactionAo.getFromCustomerId() > 0){
            result.add(IncAssetCoinBO.builder()
                    .customerId(makeTransactionAo.getFromCustomerId())
                    .coin(makeTransactionAo.getFromCoin())
                    .rice(makeTransactionAo.getFromRice())
                    .costCoin(makeTransactionAo.getFromCostCoin())
                    .build());
        }
        if (makeTransactionAo.getToCustomerId() > 0){
            result.add(IncAssetCoinBO.builder()
                    .customerId(makeTransactionAo.getToCustomerId())
                    .coin(makeTransactionAo.getToCoin())
                    .rice(makeTransactionAo.getToRice())
                    .costCoin(makeTransactionAo.getToCostCoin())
                    .build());
        }
        return result;
    }

    private boolean canIncAsset(List<IncAssetCoinBO> incAssetList){
        for (IncAssetCoinBO incAsset: incAssetList) {
            if (!canIncAsset(incAsset)){
                return false;
            }
        }
        return true;
    }

    private boolean canIncAsset(IncAssetCoinBO assetTransaction){
        if (assetTransaction.getCoin() >= 0 && assetTransaction.getRice() >=0){
            return true;
        }
        AssetCoin fromAsset = getAsset(AssetCoinQuery.builder().customerId(assetTransaction.getCustomerId()).build());
        return fromAsset.getCoin() + assetTransaction.getCoin() >= 0 && fromAsset.getRice() + assetTransaction.getRice() >= 0;
    }

    private LambdaQueryWrapper buildAssetQuery(AssetCoinQuery assetQuery){
        LambdaQueryWrapper<AssetCoin> query = new LambdaQueryWrapper<>();
        if (assetQuery.getCustomerId() != null){
            query = query.eq(AssetCoin::getCustomerId, assetQuery.getCustomerId());
        }
        return query;
    }
}
