package com.x.provider.customer.factory.vo;

import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.customer.model.vo.CustomerHomePageVO;
import com.x.provider.customer.model.vo.SimpleCustomerVO;

import java.util.List;

/**
 * @author: liushenyi
 * @date: 2022/07/28/16:17
 */
public interface CustomerVOFactory {
    CustomerHomePageVO prepare(CustomerHomePageVO dest, CustomerDTO src);
    List<SimpleCustomerVO> prepare(List<Long> uidList, Long sessionUid);
}
