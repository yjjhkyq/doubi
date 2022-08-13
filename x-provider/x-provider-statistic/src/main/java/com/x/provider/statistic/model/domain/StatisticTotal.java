package com.x.provider.statistic.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("stat_total")
public class StatisticTotal {
    @TableId
    private Long id;
    private Long longValue;
    private Double doubleValue;
    private Date startDate;
    private Integer period;
    private Integer metric;
    private String itemId;
    private Integer itemType;
}
