package com.x.provider.mc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.mc.model.domain.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageMapper extends BaseMapper<Message> {
}
