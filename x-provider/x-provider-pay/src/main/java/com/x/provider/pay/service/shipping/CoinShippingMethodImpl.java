package com.x.provider.pay.service.shipping;

import com.x.provider.api.pay.enums.ProductTypeEnum;
import com.x.provider.pay.model.bo.asset.IncAssetCoinBO;
import com.x.provider.pay.model.domain.order.Order;
import com.x.provider.pay.service.asset.AssetCoinService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service("coinShippingMethodImpl")
public class CoinShippingMethodImpl implements ShippingMethod{

    private final AssetCoinService assetService;

    public CoinShippingMethodImpl(AssetCoinService assetService){
        this.assetService = assetService;
    }

    @Override
    public void shipping(Order order) {
        assetService.incAsset(Arrays.asList(IncAssetCoinBO.builder().rice(0L).coin(order.getOrderTotal()).build()));
    }

    @Override
    public ProductTypeEnum getProductType() {
        return ProductTypeEnum.COIN;
    }
}
