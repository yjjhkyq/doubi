package com.x.provider.video.model.vo;

import lombok.Data;

@Data
public class TopicSearchItemVO {
    private long id;
    private String title;
    private int effectValue;
    private int sourceType;
    private String searchKeyWord;
    private String sourceId;
    private String topicDescription;
    private boolean systemTopic;
}
