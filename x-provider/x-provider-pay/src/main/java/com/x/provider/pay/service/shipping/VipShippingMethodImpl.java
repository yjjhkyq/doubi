package com.x.provider.pay.service.shipping;

import com.x.provider.api.pay.enums.ProductTypeEnum;
import com.x.provider.pay.model.bo.asset.IncAssetVipBO;
import com.x.provider.pay.model.domain.order.Order;
import com.x.provider.pay.model.domain.order.OrderItem;
import com.x.provider.pay.model.domain.product.ProductVip;
import com.x.provider.pay.service.asset.AssetVipService;
import com.x.provider.pay.service.order.OrderService;
import com.x.provider.pay.service.product.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("vipShippingMethodImpl")
public class VipShippingMethodImpl implements ShippingMethod{

    private final AssetVipService assetService;
    private final ProductService<ProductVip> productService;
    private final OrderService orderService;

    public VipShippingMethodImpl(AssetVipService assetService,
                                 ProductService<ProductVip> productService,
                                 OrderService orderService){
        this.assetService = assetService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void shipping(Order order) {
        List<OrderItem> orderItemList = orderService.listOrderItem(order.getId());
        List<IncAssetVipBO> incAssetVipList = new ArrayList<>(orderItemList.size());
        orderItemList.stream().forEach(item -> {
            ProductVip productVip = productService.getById(item.getProductId());
            assetService.incAsset(Arrays.asList(IncAssetVipBO.builder().customerId(order.getCustomerId()).durationDay(productVip.getDurationDay()).build()));
        });
    }

    @Override
    public ProductTypeEnum getProductType() {
        return ProductTypeEnum.VIP;
    }

}
