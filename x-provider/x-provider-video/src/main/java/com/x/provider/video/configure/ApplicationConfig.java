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

    @Value("${follow.video.default.init.interval:100}")
    private int followVideoDefaultInitIntervalDay;

    @Value("${follow.video.init.min.interval:10}")
    private int followVideoInitMinIntervalMinute;

    @Value("${sql.in.max.element:1000}")
    private int sqlInMaxElement;

    @Value("${hot.video.show.size:1000}")
    private int hotVideoShowSize;

    @Value("${follow.topic.hot.video.show.size:1000}")
    private int followTopicHotVideoShowSize;

    @Value("${topic.hot.video.show.size:1000}")
    private int topicHotVideoShowSize;

    @Value("${recommend.video.play.full.min.rate:0.8}")
    private double videoPlayFullMinRate;
}
