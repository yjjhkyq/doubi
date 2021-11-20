package com.x.provider.mc.model.ao;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListNotifyAO {
    private Long senderUid;
    private Integer targetType;
}
