package com.x.provider.video.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.provider.api.finance.model.dto.SecurityDTO;
import com.x.provider.api.video.enums.TopicSourceTypeEnum;
import com.x.provider.video.mapper.TopicMapper;
import com.x.provider.video.model.domain.Topic;
import com.x.provider.video.service.TopicFillBaseService;
import org.springframework.stereotype.Service;

@Service
public class SecurityTopicFillService extends TopicFillBaseService<SecurityDTO> {

    public SecurityTopicFillService(TopicMapper topicMapper){
        super(topicMapper);
    }

    @Override
    public String getTopicSourceId(SecurityDTO securityDTO) {
        return String.valueOf(securityDTO.getId());
    }


    @Override
    public Topic prepare(Topic topic, SecurityDTO securityDTO) {
        if (topic == null){
            topic = new Topic();
            topic.setSystemTopic(true);
            topic.setEffectValue(getEffectValue(true));
            topic.setSourceType(TopicSourceTypeEnum.SECURITY.ordinal());
            topic.setSourceId(String.valueOf(securityDTO.getId()));
            topic.setSystemTopic(true);
        }
        topic.setTitle(securityDTO.getName());
        topic.setSearchKeyWord(StrUtil.format("{},{},{},{}", securityDTO.getName(), securityDTO.getFullName(), securityDTO.getSymbol(), securityDTO.getCnSpell()));
        return topic;
    }

    @Override
    public TopicSourceTypeEnum getTopicSourceType() {
        return TopicSourceTypeEnum.SECURITY;
    }


}
