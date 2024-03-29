package com.x.provider.api.mc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRawDTO {
    private Integer messageClass;
    private String messageType;
    private String jsonData;
    @Builder.Default
    private Long toCustomerId = 0L;
    @Builder.Default
    private Long toGroupId = 0L;
}
