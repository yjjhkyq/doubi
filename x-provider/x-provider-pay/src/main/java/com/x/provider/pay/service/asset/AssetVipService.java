package com.x.provider.pay.service.asset;

import com.x.provider.pay.model.bo.asset.IncAssetVipBO;
import com.x.provider.pay.model.domain.asset.AssetVip;

import java.util.List;

public interface AssetVipService {
    void incAsset(List<IncAssetVipBO> incAssetVipAO);
    AssetVip get(Long customerId);
}
