package com.x.provider.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.pay.model.bo.asset.IncAssetCoinBO;
import com.x.provider.pay.model.domain.asset.AssetCoin;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetCoinMapper extends BaseMapper<AssetCoin> {
    Integer incAsset(IncAssetCoinBO assetTransaction);
}
