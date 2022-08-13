package com.x.provider.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.provider.customer.mapper.RegionMapper;
import com.x.provider.customer.model.domain.Region;
import com.x.provider.customer.model.query.RegionQuery;
import com.x.provider.customer.service.RegionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: liushenyi
 * @date: 2022/08/10/17:33
 */
@Service
public class RegionServiceImpl implements RegionService {

    private final RegionMapper regionMapper;

    public RegionServiceImpl(RegionMapper regionMapper){
        this.regionMapper = regionMapper;
    }

    @Override
    public List<Region> listRegion(Integer countryId, Integer leLevel) {
        final List<Region> regionList = regionMapper.selectList(null);
        final Region country = regionList.stream().filter(item -> item.getId().equals(countryId)).findFirst().get();
        return list(RegionQuery.builder().countryId(countryId).leLevel(leLevel).build());
    }

    public List<Region> list(RegionQuery regionQuery){
        return regionMapper.selectList(buildQuery(regionQuery));
    }

    public LambdaQueryWrapper<Region> buildQuery(RegionQuery regionQuery){
        LambdaQueryWrapper<Region> queryWrapper = new LambdaQueryWrapper<>();
        if (regionQuery.getCountryId() != null){
            queryWrapper = queryWrapper.eq(Region::getCountryId, regionQuery.getCountryId());
        }
        if (regionQuery.getLeLevel() != null){
            queryWrapper = queryWrapper.eq(Region::getLevel, regionQuery.getLeLevel());
        }
        return queryWrapper;
    }
}
