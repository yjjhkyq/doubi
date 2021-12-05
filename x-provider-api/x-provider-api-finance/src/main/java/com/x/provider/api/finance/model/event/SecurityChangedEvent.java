package com.x.provider.api.finance.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.x.provider.api.finance.model.dto.SecurityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: liushenyi
 * @date: 2021/12/03/10:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityChangedEvent {
    private FinanceDataChangedEventEnum financeDataChangedEventEnum;
    private Long id;
    private String code;
    private String symbol;
    private String name;
    private String fullName;
    private String enName;
    private String cnSpell;
    private String exchange;
    private String type;
    private String parentCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdOnUtc;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedOnUtc;
}
