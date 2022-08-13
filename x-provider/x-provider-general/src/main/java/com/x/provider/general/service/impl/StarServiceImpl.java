package com.x.provider.general.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.BeanUtil;
import com.x.provider.api.common.enums.ItemTypeEnum;
import com.x.provider.api.general.model.dto.ListStarRequestDTO;
import com.x.provider.api.general.model.dto.StarRequestDTO;
import com.x.provider.api.general.model.dto.CommentDTO;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.general.comstant.GeneralEventTopic;
import com.x.provider.general.mapper.StarMapper;
import com.x.provider.general.model.domain.Comment;
import com.x.provider.general.model.domain.Star;
import com.x.provider.general.service.CommentService;
import com.x.provider.general.service.CommentStatService;
import com.x.provider.general.service.ItemStatService;
import com.x.provider.general.service.StarService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class StarServiceImpl implements StarService {

    private final StarMapper starMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ItemStatService itemStatService;
    private final CommentStatService commentStatService;
    private final CommentService commentService;

    public StarServiceImpl(StarMapper starMapper,
                           KafkaTemplate<String, Object> kafkaTemplate,
                           ItemStatService itemStatService,
                           CommentStatService commentStatService,
                           CommentService commentService){
        this.starMapper = starMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.itemStatService = itemStatService;
        this.commentStatService = commentStatService;
        this.commentService = commentService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean star(StarRequestDTO starAO) {
        Comment comment = null;
        if (starAO.getItemType() == ItemTypeEnum.COMMENT.getValue()){
            comment = commentService.getById(starAO.getItemId());
            if (comment == null){
                return false;
            }
        }
        Star starEntity = starMapper.selectOne(buildQuery(starAO.getItemId(), starAO.getStarCustomerId(), starAO.getItemType(), null));
        boolean firstStar = starEntity == null;
        if (starEntity == null || starEntity.isStar() == starAO.isStar()){
            return false;
        }
        if (starEntity == null){
            starEntity = BeanUtil.prepare(starAO, Star.class);
            starMapper.insert(starEntity);
        }
        else{
            starEntity.setStar(starAO.isStar());
            starMapper.updateById(starEntity);
        }
        commentStatService.onStar(starEntity);
        itemStatService.onStar(starEntity);
        StarEvent starEvent = BeanUtil.prepare(starEntity, StarEvent.class);
        starEvent.setItemId(String.valueOf(starEntity.getItemId()));
        starEvent.setFirstStar(firstStar);
        starEvent.setComment(BeanUtil.prepare(comment, CommentDTO.class));
        kafkaTemplate.send(GeneralEventTopic.TOPIC_NAME_STAR, StrUtil.format("{}:{}", starEntity.getItemType(), starEntity.getItemId()), starEvent);
        return true;
    }

    @Override
    public boolean isStarred(int itemType, long itemId, long customerId){
        Star star = getStar(itemId, customerId, itemType);
        return star != null && star.isStar();
    }

    @Override
    public List<Star> listStar(ListStarRequestDTO listStarAO) {
        LambdaQueryWrapper<Star> query = buildQuery(0L, listStarAO.getStarCustomerId(), listStarAO.getItemType(), listStarAO.getItemIdList());
        return starMapper.selectList(query);
    }

    private Star getStar(long itemId, long starCustomerId, int itemType){
        LambdaQueryWrapper<Star> query = buildQuery(itemId, starCustomerId, itemType, null);
        return starMapper.selectOne(query);
    }

    private LambdaQueryWrapper<Star> buildQuery(long itemId, long starCustomerId, int itemType, List<Long> itemIdList) {
        LambdaQueryWrapper<Star> query = new LambdaQueryWrapper<>();
        if (itemId > 0){
            query = query.eq(Star::getItemId, itemId);
        }
        if (starCustomerId > 0){
            query = query.eq(Star::getStarCustomerId, starCustomerId);
        }
        if (itemType > 0){
            query = query.eq(Star::getItemType, itemType);
        }
        if (!CollectionUtils.isEmpty(itemIdList)){
            query = query.in(Star::getItemId, itemIdList);
        }
        return query;
    }
}
