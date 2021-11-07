package com.x.provider.customer.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RefreshScope
public class ApplicationConfig {
    @Value("#{'${auth.ignore.urls}'.split(',')}")
    private List<String> authIgnoreUrls;

    private String defaultAvatarId = "0/customer_icon_default.jpeg";
    private String defaultPersonalHomePageBackgroundId = "0/home_page_background_default.jpg";
    private String defaultNickName = "牛牛";

    public List<String> getAuthIgnoreUrls() {
        if (authIgnoreUrls == null){
            authIgnoreUrls = new ArrayList<>();
        }
        return authIgnoreUrls;
    }

    public void setAuthIgnoreUrls(List<String> authIgnoreUrls) {
        this.authIgnoreUrls = authIgnoreUrls;
    }

    public String getDefaultAvatarId() {
        return defaultAvatarId;
    }

    public void setDefaultAvatarId(String defaultAvatarId) {
        this.defaultAvatarId = defaultAvatarId;
    }

    public String getDefaultPersonalHomePageBackgroundId() {
        return defaultPersonalHomePageBackgroundId;
    }

    public void setDefaultPersonalHomePageBackgroundId(String defaultPersonalHomePageBackgroundId) {
        this.defaultPersonalHomePageBackgroundId = defaultPersonalHomePageBackgroundId;
    }

    public String getDefaultNickName() {
        return defaultNickName;
    }

    public void setDefaultNickName(String defaultNickName) {
        this.defaultNickName = defaultNickName;
    }
}
