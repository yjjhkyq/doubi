package com.x.provider.customer.service;

import com.x.provider.customer.model.domain.Region;

import java.util.List;

/**
 * @author: liushenyi
 * @date: 2022/08/10/17:33
 */
public interface RegionService {
    List<Region> listRegion(Integer countryId, Integer leLevel);
}
