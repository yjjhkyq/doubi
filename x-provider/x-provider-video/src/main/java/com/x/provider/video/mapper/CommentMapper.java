package com.x.provider.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.video.model.domain.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMapper extends BaseMapper<Comment> {
}
