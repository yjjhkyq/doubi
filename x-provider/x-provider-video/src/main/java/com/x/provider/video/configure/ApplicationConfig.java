package com.x.provider.video.configure;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
public class ApplicationConfig {

    @Value("${topic.search.result.limit}")
    private int topSearchResultLimit;
}
