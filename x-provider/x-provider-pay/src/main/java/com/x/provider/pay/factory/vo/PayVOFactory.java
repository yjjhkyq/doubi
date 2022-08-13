package com.x.provider.pay.factory.vo;

import com.x.provider.api.pay.model.dto.CreateOrderDTO;
import com.x.provider.pay.model.vo.CreateOrderVO;

public interface PayVOFactory {
    CreateOrderDTO prepare(CreateOrderVO createOrderVO, Long sessionCustomerId);
}
