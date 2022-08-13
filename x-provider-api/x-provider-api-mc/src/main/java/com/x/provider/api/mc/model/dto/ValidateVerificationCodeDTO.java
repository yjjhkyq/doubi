package com.x.provider.api.mc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateVerificationCodeDTO {
    private String phoneNumber;
    private String sms;
}
