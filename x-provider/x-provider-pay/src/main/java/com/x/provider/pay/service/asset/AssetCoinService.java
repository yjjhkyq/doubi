package com.x.provider.pay.service.asset;

import com.x.provider.api.pay.model.dto.CreateTransactionDTO;
import com.x.provider.pay.model.bo.asset.IncAssetCoinBO;
import com.x.provider.pay.model.domain.asset.AssetCoin;

import java.util.List;

public interface AssetCoinService {
    void incAsset(List<IncAssetCoinBO> incAssetList);
    AssetCoin getAsset(Long customerId);
    void initAsset(Long customerId);
    Long transaction(CreateTransactionDTO makeTransactionAo);
}
