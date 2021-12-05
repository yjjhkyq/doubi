package com.x.provider.api.customer.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAttributeEvent {
    private String nickName;
    private String signature;
    private String avatarId;
    private String avatarUrl;
    private String personalHomePageBackgroundId;
}
