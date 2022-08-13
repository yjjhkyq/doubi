package com.x.provider.pay.service.product;

import com.x.provider.pay.mapper.ProductMapper;
import com.x.provider.pay.mapper.ProductVipMapper;
import com.x.provider.pay.model.domain.product.Product;
import com.x.provider.pay.model.domain.product.ProductVip;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("productVipService")
public class ProductVipServiceImpl implements ProductService<ProductVip> {

    private final ProductVipMapper productVipMapper;

    public ProductVipServiceImpl(ProductVipMapper productVipMapper){
        this.productVipMapper = productVipMapper;
    }

    @Override
    public List<ProductVip> listProduct(Integer productType) {
        return null;
    }

    @Override
    public ProductVip getById(Long id) {
        return productVipMapper.selectById(id);
    }
}
