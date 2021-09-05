package com.x.provider.video.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.provider.api.finance.model.dto.IndustryDTO;
import com.x.provider.api.finance.model.dto.SecurityDTO;
import com.x.provider.api.video.enums.TopicSourceTypeEnum;
import com.x.provider.video.mapper.TopicMapper;
import com.x.provider.video.model.domain.Topic;
import com.x.provider.video.service.TopicFillBaseService;
import org.springframework.stereotype.Service;

@Service
public class IndustryTopicFillServiceImpl extends TopicFillBaseService<IndustryDTO> {

    public IndustryTopicFillServiceImpl(TopicMapper topicMapper){
        super(topicMapper);
    }

    @Override
    public String getTopicSourceId(IndustryDTO industryDTO) {
        return String.valueOf(industryDTO.getId());
    }


    @Override
    public Topic prepare(Topic topic, IndustryDTO industryDTO) {
        if (topic == null){
            topic = new Topic();
            topic.setSystemTopic(true);
            topic.setEffectValue(getEffectValue(true));
            topic.setSourceType(TopicSourceTypeEnum.INDUSTRY.ordinal());
            topic.setSourceId(String.valueOf(industryDTO.getId()));
            topic.setSystemTopic(true);
        }
        topic.setTitle(industryDTO.getName());
        topic.setSearchKeyWord(StrUtil.format("{},{},{}", industryDTO.getName(), industryDTO.getCode(), industryDTO.getCnSpell()));
        return topic;
    }

    @Override
    public TopicSourceTypeEnum getTopicSourceType() {
        return TopicSourceTypeEnum.INDUSTRY;
    }


}
