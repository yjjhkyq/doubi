package com.x.provider.api.mc.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateVerificationCodeAO {
    private String phoneNumber;
    private String sms;
}
