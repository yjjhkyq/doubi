package com.x.provider.pay.service.product;

import com.x.provider.pay.model.domain.product.Product;

import java.util.List;

public interface ProductService<T> {
    List<T> listProduct(Integer productType);
    T getById(Long id);
}
