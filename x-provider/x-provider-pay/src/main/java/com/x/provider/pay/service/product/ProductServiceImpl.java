package com.x.provider.pay.service.product;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.provider.pay.mapper.ProductMapper;
import com.x.provider.pay.model.domain.product.Product;
import com.x.provider.pay.model.query.product.ProductQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("productService")
public class ProductServiceImpl implements ProductService<Product> {

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper){
        this.productMapper = productMapper;
    }

    @Override
    public List<Product> listProduct(Integer productType) {
        return listProduct(ProductQuery.builder().productType(productType).build());
    }

    @Override
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }

    public List<Product> listProduct(ProductQuery productQuery){
        return productMapper.selectList(build(productQuery));
    }

    private LambdaQueryWrapper<Product> build(ProductQuery productQuery){
        LambdaQueryWrapper<Product> query = new LambdaQueryWrapper<>();
        if (productQuery.getProductType() != null){
            query = query.eq(Product::getProductType, productQuery.getProductType());
        }
        return query;
    }
}
