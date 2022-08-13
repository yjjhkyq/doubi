package com.x.provider.pay.model.bo.payment;

import com.x.provider.api.pay.enums.PayMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayNotifyBO {
    private PayMethodEnum payMethod;
    private Map<String, Object> body;
}
