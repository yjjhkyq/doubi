package com.x.provider.api.finance.model.event;

import com.x.provider.api.finance.enums.FinanceDataTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceDataChangedEvent {
    private FinanceDataChangedEventEnum financeDataChangedEventEnum;
    private List<String> ids;
    private FinanceDataTypeEnum financeDataType;
}
