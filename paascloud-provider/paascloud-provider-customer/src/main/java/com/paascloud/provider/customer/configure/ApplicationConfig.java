package com.paascloud.provider.customer.configure;

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

    public List<String> getAuthIgnoreUrls() {
        if (authIgnoreUrls == null){
            authIgnoreUrls = new ArrayList<>();
        }
        return authIgnoreUrls;
    }

    public void setAuthIgnoreUrls(List<String> authIgnoreUrls) {
        this.authIgnoreUrls = authIgnoreUrls;
    }
}
