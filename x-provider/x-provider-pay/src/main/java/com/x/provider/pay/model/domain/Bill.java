package com.x.provider.pay.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import com.x.provider.api.pay.model.dto.BillDto;
import com.x.provider.pay.enums.bill.BillStatus;
import com.x.provider.pay.enums.bill.BillType;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bill")
public class Bill extends BaseEntity {

    @TableId
    private long id;

    private String serialNumber;  // IdWorker.getTimeId();

    private long customerId;  // 用户id
    private long toCustomerId;  // 引发订单的目标id, 例如向xx转账

    private BillType type;
    private BillStatus status;

    private BigDecimal amount;

    private String comment;  // 备注

    public BillDto toDto() {
        BillDto billDto = new BillDto();
        BeanUtils.copyProperties(this, billDto);
        return billDto;
    }
}

