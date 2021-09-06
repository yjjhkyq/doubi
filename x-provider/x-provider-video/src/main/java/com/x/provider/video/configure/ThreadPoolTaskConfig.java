package com.x.provider.video.configure;

import com.x.core.utils.ThreadPoolUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolTaskConfig {
    @Bean
    public Executor executor(){
        return ThreadPoolUtil.createExecutor("video-default-executor");
    }
}
