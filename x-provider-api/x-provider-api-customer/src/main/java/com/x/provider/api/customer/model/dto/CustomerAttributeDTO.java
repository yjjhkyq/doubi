package com.x.provider.api.customer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAttributeDTO {
    private String nickName;
    private String signature;
    private String avatarId;
    private String avatarUrl;
    private String personalHomePageBackgroundId;
    private String personalHomePageBackgroundUrl;
    private Integer gender;
}
