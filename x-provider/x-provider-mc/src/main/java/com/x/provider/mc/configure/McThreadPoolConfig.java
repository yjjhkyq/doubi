package com.x.provider.mc.configure;

import com.x.core.utils.ThreadPoolUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;

@Configuration
public class McThreadPoolConfig {
    @Bean(name = "mcDefaultExecutor")
    public Executor executor(){
        return ThreadPoolUtil.createExecutor("mc-default-executor");
    }
}
