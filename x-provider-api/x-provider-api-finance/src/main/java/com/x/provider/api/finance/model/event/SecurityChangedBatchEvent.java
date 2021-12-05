package com.x.provider.api.finance.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: liushenyi
 * @date: 2021/12/03/10:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityChangedBatchEvent {
    private List<SecurityChangedEvent> securityChangedEventList;
}
