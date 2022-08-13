package com.x.provider.statistic.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: liushenyi
 * @date: 2022/08/08/14:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncStatisticTotalBO {
    private Long id;
    private Long longValue;
    private Double doubleValue;
}
