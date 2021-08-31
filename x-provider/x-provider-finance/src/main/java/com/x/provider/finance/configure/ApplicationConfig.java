package com.x.provider.finance.configure;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@RefreshScope
public class ApplicationConfig {

    @Value("${tushare.token}")
    private String tushareToken;

    public String getTushareToken() {
        return tushareToken;
    }

    public void setTushareToken(String tushareToken) {
        this.tushareToken = tushareToken;
    }
}
