package com.x.provider.video.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.provider.api.video.enums.TopicSourceTypeEnum;
import com.x.provider.video.mapper.TopicMapper;
import com.x.provider.video.model.domain.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class TopicFillBaseService<T> {

    public static final Map<TopicSourceTypeEnum, Integer> TOPIC_SOURCE_EFFECT_VALUE_MAP = Map.of(TopicSourceTypeEnum.SECURITY, 100, TopicSourceTypeEnum.INDUSTRY, 80,
            TopicSourceTypeEnum.CUSTOMER_CUSTOMIZE, 0);

    private final TopicMapper topicMapper;

    public TopicFillBaseService(TopicMapper topicMapper){
        this.topicMapper = topicMapper;
    }

    public void initTopic(List<T> source){
        if (source.isEmpty()){
            return;
        }
        var topicMap = listTopic(getTopicSourceType()).stream().collect(Collectors.toMap(Topic::getSourceId, item -> item));
        var addItemList = new ArrayList<Topic>(source.size());
        var updateItemList = new ArrayList<Topic>(topicMap.size());
        source.forEach(item ->{
            var topic = topicMap.get(getTopicSourceId(item));
            var topicNew = prepare(topic, item);
            if (topic == null){
                addItemList.add(topicNew);
                return;
            }
            updateItemList.add(topicNew);
        });
        updateItemList.forEach(item ->{
            topicMapper.updateById(item);
        });
        addItemList.forEach(item -> {
            topicMapper.insert(item);
        });
    }

    public abstract String getTopicSourceId(T t);
    public abstract Topic prepare(Topic topic, T t);
    public abstract TopicSourceTypeEnum getTopicSourceType();
    public int getEffectValue(){
        return TOPIC_SOURCE_EFFECT_VALUE_MAP.getOrDefault(getTopicSourceType(), 0);
    }

    private List<Topic> listTopic(TopicSourceTypeEnum topicSourceType){
        var query = new LambdaQueryWrapper<Topic>();
        if (topicSourceType != null){
            query = query.eq(Topic::getSourceType, topicSourceType.ordinal());
        }
        return topicMapper.selectList(query);
    }
}
