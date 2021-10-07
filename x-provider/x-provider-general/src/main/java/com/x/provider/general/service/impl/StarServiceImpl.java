package com.x.provider.general.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.BeanUtil;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.general.model.event.StarRequestEvent;
import com.x.provider.api.statistic.constants.StatisticEventTopic;
import com.x.provider.api.statistic.model.event.StatisticTotalEvent;
import com.x.provider.general.comstant.GeneralEventTopic;
import com.x.provider.general.enums.StarStatisticEnum;
import com.x.provider.general.mapper.StarMapper;
import com.x.provider.general.model.domain.Star;
import com.x.provider.general.service.StarService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StarServiceImpl implements StarService {

    private final StarMapper starMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public StarServiceImpl(StarMapper starMapper,
                           KafkaTemplate<String, Object> kafkaTemplate){
        this.starMapper = starMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public boolean star(long associationItemId, long itemId, long starCustomerId, int itemType, boolean star) {
        Star starEntity = getStar(0, itemId, starCustomerId, itemType);
        if (starEntity == null){
            if (star){
                starEntity = Star.builder().isStar(star).itemId(itemId).itemType(itemType).starCustomerId(starCustomerId).associationItemId(associationItemId).build();
                starMapper.insert(starEntity);
                return true;
            }
            return false;
        }
        if (starEntity.isStar() == star){
            return false;
        }
        starEntity.setStar(star);
        starMapper.updateById(Star.builder().id(starEntity.getId()).isStar(star).build());
        return true;
    }

    @Override
    public void onStarRequest(StarRequestEvent starRequestEvent) {
        boolean firstStar = getStar(0, starRequestEvent.getItemId(), starRequestEvent.getStarCustomerId(), starRequestEvent.getItemType()) == null;
        boolean starSuccess = star(starRequestEvent.getAssociationItemId(), starRequestEvent.getItemId(), starRequestEvent.getStarCustomerId(), starRequestEvent.getItemType(), starRequestEvent.isStar());
        if (starSuccess){
            long value = starRequestEvent.isStar() ? 1L : -1L;
            List<StarStatisticEnum> starStatistics = StarStatisticEnum.valueOf(starRequestEvent.getItemType());
            if (!starStatistics.isEmpty()){
                starStatistics.forEach(item ->{
                    kafkaTemplate.send(StatisticEventTopic.TOPIC_NAME_STAT_TOTAL_EVENT, StrUtil.format("{}:{}", starRequestEvent.getItemType(), starRequestEvent.getItemId()),
                            StatisticTotalEvent.builder().longValue(value).statisticObjectClassEnum(item.getStatisticObjectClass().getValue())
                            .statisticObjectId(String.valueOf(starRequestEvent.getItemId())).statisticPeriodEnum(item.getStatisticPeriod().getValue())
                            .statTotalItemNameEnum(item.getStatTotalItemName().getValue()).build());
                });
            }

            Star star = getStar(0, starRequestEvent.getItemId(), starRequestEvent.getStarCustomerId(), starRequestEvent.getItemType());
            StarEvent starEvent = BeanUtil.prepare(star, StarEvent.class);
            starEvent.setFirstStar(firstStar);
            kafkaTemplate.send(GeneralEventTopic.TOPIC_NAME_STAR, StrUtil.format("{}:{}", starRequestEvent.getItemType(), starRequestEvent.getItemId()), starEvent);
        }
    }

    @Override
    public boolean isStarred(int itemType, long itemId, long customerId){
        Star star = getStar(0, itemId, customerId, itemType);
        return star != null && star.isStar();
    }

    @Override
    public List<Star> listStar(long associationItemId, long starCustomerId) {
        return listStar(associationItemId, 0, starCustomerId, 0);
    }

    private Star getStar(long associationItemId, long itemId, long starCustomerId, int itemType){
        LambdaQueryWrapper<Star> query = buildQuery(associationItemId, itemId, starCustomerId, itemType);
        return starMapper.selectOne(query);
    }

    private List<Star> listStar(long associationItemId, long itemId, long starCustomerId, int itemType){
        LambdaQueryWrapper<Star> query = buildQuery(associationItemId, itemId, starCustomerId, itemType);
        return starMapper.selectList(query);
    }

    private LambdaQueryWrapper<Star> buildQuery(long associationItemId, long itemId, long starCustomerId, int itemType) {
        LambdaQueryWrapper<Star> query = new LambdaQueryWrapper<>();
        if (associationItemId > 0){
            query = query.eq(Star::getAssociationItemId, associationItemId);
        }
        if (itemId > 0){
            query = query.eq(Star::getItemId, itemId);
        }
        if (starCustomerId > 0){
            query = query.eq(Star::getStarCustomerId, starCustomerId);
        }
        if (itemType > 0){
            query = query.eq(Star::getItemType, itemType);
        }
        return query;
    }
}
