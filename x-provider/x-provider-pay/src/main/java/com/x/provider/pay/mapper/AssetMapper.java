package com.x.provider.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.pay.model.ao.IncAssetAO;
import com.x.provider.pay.model.domain.Asset;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetMapper extends BaseMapper<Asset> {
    Integer incAsset(IncAssetAO assetTransaction);
}
