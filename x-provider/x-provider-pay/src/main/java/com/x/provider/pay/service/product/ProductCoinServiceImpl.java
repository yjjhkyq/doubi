package com.x.provider.pay.service.product;

import com.x.provider.pay.mapper.ProductCoinMapper;
import com.x.provider.pay.mapper.ProductMapper;
import com.x.provider.pay.model.domain.product.ProductCoin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("productCoinpService")
public class ProductCoinServiceImpl implements ProductService<ProductCoin> {

    private final ProductCoinMapper productCoinMapper;

    public ProductCoinServiceImpl(ProductCoinMapper productCoinMapper) {
        this.productCoinMapper = productCoinMapper;
    }

    @Override
    public List<ProductCoin> listProduct(Integer productType) {
        return null;
    }

    @Override
    public ProductCoin getById(Long id) {
        return productCoinMapper.selectById(id);
    }
}
