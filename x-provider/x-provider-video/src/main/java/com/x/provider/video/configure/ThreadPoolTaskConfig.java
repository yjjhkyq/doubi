package com.x.provider.video.configure;

import com.x.core.utils.ThreadPoolUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolTaskConfig {
    @Bean(name = "videoDefaultExecutor")
    public Executor executor(){
        return ThreadPoolUtil.createExecutor("video-default-executor");
    }
}
