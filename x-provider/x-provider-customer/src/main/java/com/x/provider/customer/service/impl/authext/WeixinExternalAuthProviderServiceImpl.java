package com.x.provider.customer.service.impl.authext;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.JsonUtil;
import com.x.provider.customer.configure.WeixinConfig;
import com.x.provider.customer.enums.ExternalAuthProviderEnum;
import com.x.provider.customer.enums.UserResultCode;
import com.x.provider.customer.model.ao.ExternalAuthenticationAO;
import com.x.provider.customer.model.domain.ExternalAuthenticationRecord;
import com.x.provider.customer.service.ExternalAuthProviderService;
import com.x.provider.customer.service.ExternalAuthenticationRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class WeixinExternalAuthProviderServiceImpl implements ExternalAuthProviderService {

    private static final String URL_JS_CODE_2_SESSION = "https://api.weixin.qq.com/sns/jscode2session?appid={}&secret={}&js_code={}&grant_type=authorization_code";

    private final ExternalAuthenticationRecordService externalAuthenticationRecordService;
    private final WeixinConfig weixinConfig;


    public WeixinExternalAuthProviderServiceImpl(ExternalAuthenticationRecordService externalAuthenticationRecordService,
                                                 WeixinConfig weixinConfig){
        this.externalAuthenticationRecordService = externalAuthenticationRecordService;
        this.weixinConfig = weixinConfig;
    }

    @Override
    public ExternalAuthenticationRecord authenticate(ExternalAuthenticationAO externalAuthenticationAO) {
        Map session = JsonUtil.parseObject(HttpUtil.get(StrUtil.format(URL_JS_CODE_2_SESSION, weixinConfig.getMicroAppId(), weixinConfig.getMicroAppSecret(),
                externalAuthenticationAO.getOauthAccessToken()))
                , Map.class);
        if (session.containsKey("errcode") && (int)session.get("errcode") != 0){
            log.error("weixin auth failed, js code:{} error code:{}, error msg:{} ", externalAuthenticationAO.getOauthAccessToken(), session.get("errcode"), session.get("errmsg"));
            ApiAssetUtil.isTrue(false, UserResultCode.WEIXIN_MICRO_APP_AUTH_FAILED);
        }
        String openid = String.valueOf(session.get("openid"));
        ExternalAuthenticationRecord externalAuthenticationRecord = externalAuthenticationRecordService.get(null, externalAuthenticationAO.getProvider(), openid, null);
        if (externalAuthenticationRecord == null){
            externalAuthenticationRecord = ExternalAuthenticationRecord.builder().provider(externalAuthenticationAO.getProvider())
                    .externalIdentifier(openid).build();
        }
        externalAuthenticationRecord.setOauthAccessToken(externalAuthenticationAO.getOauthAccessToken());
        return externalAuthenticationRecord;
    }

    @Override
    public boolean support(ExternalAuthenticationAO externalAuthenticationAO) {
        return externalAuthenticationAO.getProvider().equals(ExternalAuthProviderEnum.WX_MICRO_APP.getValue());
    }

}
