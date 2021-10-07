package com.x.provider.statistic.configure;

import com.x.core.utils.ThreadPoolUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolTaskConfig {
    @Bean
    public Executor executor(){
        return ThreadPoolUtil.createExecutor("statistic-default-executor");
    }
}
