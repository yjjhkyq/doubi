package com.x.provider.customer.factory.vo.impl;

import com.x.core.utils.BeanUtil;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.model.dto.ListSimpleCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import com.x.provider.customer.factory.dto.CustomerFactory;
import com.x.provider.customer.factory.vo.CustomerVOFactory;
import com.x.provider.customer.model.vo.CustomerHomePageVO;
import com.x.provider.customer.model.vo.CustomerStatVO;
import com.x.provider.customer.model.vo.GenericAttributeVO;
import com.x.provider.customer.model.vo.SimpleCustomerVO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: liushenyi
 * @date: 2022/07/28/16:18
 */
@Service
public class CustomerVOFactoryImpl implements CustomerVOFactory {

    private final CustomerFactory customerFactory;

    public CustomerVOFactoryImpl(CustomerFactory customerFactory){
        this.customerFactory = customerFactory;
    }

    @Override
    public CustomerHomePageVO prepare(CustomerHomePageVO dest, CustomerDTO src) {
        if (dest == null){
            dest = new CustomerHomePageVO();
        }
        BeanUtil.prepare(src, dest);
        dest.setAttributes(BeanUtil.prepare(src.getCustomerAttributeList(), GenericAttributeVO.class));
        dest.setStatistic(BeanUtil.prepare(dest.getStatistic(), CustomerStatVO.class));
        dest.setCustomerRelation(dest.getCustomerRelation());
        return dest;
    }

    @Override
    public List<SimpleCustomerVO> prepare(List<Long> uidList, Long sessionUid) {
        final Map<Long, SimpleCustomerDTO> simpleCustomerMap = customerFactory.prepare(ListSimpleCustomerRequestDTO.builder().customerIds(uidList).sessionCustomerId(sessionUid)
                .customerOptions(Arrays.asList(CustomerOptions.CUSTOMER.name(), CustomerOptions.CUSTOMER_RELATION.name(), CustomerOptions.CUSTOMER_ATTRIBUTE.name())).build());
        return BeanUtil.prepare(simpleCustomerMap.values(), SimpleCustomerVO.class);
    }

}
