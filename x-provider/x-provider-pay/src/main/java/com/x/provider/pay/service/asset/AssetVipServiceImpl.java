package com.x.provider.pay.service.asset;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.DateUtils;
import com.x.provider.pay.mapper.AssetVipMapper;
import com.x.provider.pay.model.bo.asset.IncAssetVipBO;
import com.x.provider.pay.model.domain.asset.AssetVip;
import com.x.provider.pay.model.query.asset.AssetCoinQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AssetVipServiceImpl implements AssetVipService{

    private final AssetVipMapper assetVipMapper;

    public AssetVipServiceImpl(AssetVipMapper assetVipMapper){
        this.assetVipMapper = assetVipMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void incAsset(List<IncAssetVipBO> incAssetVipAO) {
        for (IncAssetVipBO item: incAssetVipAO) {
            AssetVip assetVip = get(AssetCoinQuery.builder().customerId(item.getCustomerId()).build());
            if (assetVip == null){
                assetVip = AssetVip.builder().customerId(item.getCustomerId()).expireDate(new Date()).build();
            }
            assetVip.setExpireDate(DateUtils.addDays(assetVip.getExpireDate(), item.getDurationDay()));
            save(assetVip);
        }
    }

    @Override
    public AssetVip get(Long customerId) {
        return get(AssetCoinQuery.builder().customerId(customerId).build());
    }

    public void save(AssetVip assetVip){
        if (assetVip.getId() != null){
            assetVipMapper.insert(assetVip);
        }
        else{
            assetVipMapper.updateById(assetVip);
        }
    }

    AssetVip get(AssetCoinQuery query){
        return assetVipMapper.selectOne(build(query));
    }

    public LambdaQueryWrapper<AssetVip> build(AssetCoinQuery assetQuery){
        LambdaQueryWrapper<AssetVip> query = new LambdaQueryWrapper<>();
        if (assetQuery.getCustomerId() != null){
            query = query.eq(AssetVip::getCustomerId, assetQuery.getCustomerId());
        }
        return query;
    }
}
