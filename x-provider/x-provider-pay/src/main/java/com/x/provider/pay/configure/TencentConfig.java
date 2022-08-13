package com.x.provider.pay.configure;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class TencentConfig {

    @Value("${tencent.app.id}")
    private String appId;

    @Value("${tencent.pay.mchid}")
    private String mchid;

    @Value("${tencent.pay.mchserialno}")
    private String mchSerialNo;

    @Value("${tencent.pay.apikey}")
    private String apiKey;

    @Value("${tencent.pay.privatekey}")
    private String privateKey;

    @Value("${tencent.pay.notifyurl}")
    private String notifyUrl;

}
