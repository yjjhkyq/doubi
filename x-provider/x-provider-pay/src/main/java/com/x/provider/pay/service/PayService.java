package com.x.provider.pay.service;

import com.x.provider.api.pay.model.ao.TransactionAo;
import com.x.provider.api.pay.model.dto.TransactionDTO;
import com.x.provider.pay.model.domain.Asset;
import com.x.provider.pay.model.query.AssetQuery;

public interface PayService {
    TransactionDTO transaction(TransactionAo transaction);
    Asset getAsset(AssetQuery assetQuery);
    void initAsset(Long customerId);
}
