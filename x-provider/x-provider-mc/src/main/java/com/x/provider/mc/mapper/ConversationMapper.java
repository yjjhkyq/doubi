package com.x.provider.mc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.mc.model.domain.Conversation;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationMapper extends BaseMapper<Conversation> {
    Long sumUnreadCount(Long ownerCustomerId);
}
