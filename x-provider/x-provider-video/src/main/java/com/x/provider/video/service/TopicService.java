package com.x.provider.video.service;

import com.x.provider.api.finance.model.event.FinanceDataChangedEvent;
import com.x.provider.api.video.enums.TopicSourceTypeEnum;
import com.x.provider.video.model.domain.Topic;

import java.util.List;
import java.util.Optional;

public interface TopicService {
    void initTopic();
    void initTopic(List<TopicSourceTypeEnum> topicSourceTypes);
    void onFinanceDataChanged(FinanceDataChangedEvent financeDataChangedEvent);
    List<Topic> searchTopic(String keyword);
    Optional<Topic> getTopic(long id, String title);
    List<Topic> listTopic(List<String> titles);
    List<Topic> listOrCreateTopics(List<String> topicTitles);
}
