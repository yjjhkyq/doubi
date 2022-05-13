package com.x.provider.video.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.BeanUtil;
import com.x.core.utils.DateUtils;
import com.x.core.utils.SpringUtils;
import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageHelper;
import com.x.core.web.page.PageList;
import com.x.provider.api.finance.enums.FinanceDataTypeEnum;
import com.x.provider.api.finance.model.ao.ListIndustryAO;
import com.x.provider.api.finance.model.ao.ListSecurityAO;
import com.x.provider.api.finance.model.event.FinanceDataChangedEvent;
import com.x.provider.api.finance.service.FinanceRpcService;
import com.x.provider.api.video.constants.VideoEventTopic;
import com.x.provider.api.video.enums.TopicSourceTypeEnum;
import com.x.provider.api.video.model.ao.ListTopicAO;
import com.x.provider.api.video.model.event.TopicBatchEvent;
import com.x.provider.api.video.model.event.TopicEvent;
import com.x.provider.video.configure.ApplicationConfig;
import com.x.provider.video.mapper.TopicCustomerFavoriteMapper;
import com.x.provider.video.mapper.TopicMapper;
import com.x.provider.video.model.domain.Topic;
import com.x.provider.video.model.domain.TopicCustomerFavorite;
import com.x.provider.video.service.TopicFillBaseService;
import com.x.provider.video.service.TopicService;
import com.x.util.ChineseCharToEn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TopicServiceImpl implements TopicService {

    private final Map<FinanceDataTypeEnum, TopicSourceTypeEnum> TOPIC_SOURCE_TYPE_MAP = Map.of(FinanceDataTypeEnum.INDUSTRY, TopicSourceTypeEnum.INDUSTRY, FinanceDataTypeEnum.SECURITY,
            TopicSourceTypeEnum.SECURITY);

    private final Map<TopicSourceTypeEnum, FinanceDataTypeEnum> FINANCE_DATA_TYPE_MAP = Map.of( TopicSourceTypeEnum.INDUSTRY, FinanceDataTypeEnum.INDUSTRY, TopicSourceTypeEnum.SECURITY,
            FinanceDataTypeEnum.SECURITY);

    private final TopicMapper topicMapper;
    private final FinanceRpcService financeRpcService;
    private final ApplicationConfig applicationConfig;
    private final TopicCustomerFavoriteMapper topicCustomerFavoriteMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TopicServiceImpl(TopicMapper topicMapper,
                            FinanceRpcService financeRpcService,
                            ApplicationConfig applicationConfig,
                            TopicCustomerFavoriteMapper topicCustomerFavoriteMapper,
                            KafkaTemplate<String, Object> kafkaTemplate){
        this.topicMapper = topicMapper;
        this.financeRpcService = financeRpcService;
        this.applicationConfig = applicationConfig;
        this.topicCustomerFavoriteMapper = topicCustomerFavoriteMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void initTopic() {
        initTopic(Arrays.asList(TopicSourceTypeEnum.values()));
    }

    @Override
    public void initTopic(List<TopicSourceTypeEnum> topicSourceTypeEnums) {
        topicSourceTypeEnums.forEach(item -> {
            var topicSourceList = listTopicSource(item, DateUtils.minDate(), Collections.EMPTY_LIST);
            initTopic(item, topicSourceList);
        });
    }

    @Override
    public void onFinanceDataChanged(FinanceDataChangedEvent financeDataChangedEvent) {
        TopicSourceTypeEnum topicSourceTypeEnum = TOPIC_SOURCE_TYPE_MAP.get(financeDataChangedEvent.getFinanceDataType());
        if (topicSourceTypeEnum == null){
            log.error("no topic source type find for finance data type:{} ", financeDataChangedEvent.getFinanceDataType());
            return;
        }
        var topicSourceList = listTopicSource(topicSourceTypeEnum, null, financeDataChangedEvent.getIds());
        initTopic(topicSourceTypeEnum, topicSourceList);
    }

    @Override
    public List<Topic> searchTopic(String keyword){
        var query = new LambdaQueryWrapper<Topic>();
        if (StringUtils.hasText(keyword)) {
            query.like(Topic::getSearchKeyWord, keyword);
        }
        query.orderByDesc(Topic::getEffectValue).last(StrUtil.format(" limit {}", applicationConfig.getTopSearchResultLimit()));
        return topicMapper.selectList(query);
    }

    @Override
    public Optional<Topic> getTopic(long id, String title) {
        var query = new LambdaQueryWrapper<Topic>();
        if (id > 0) {
            query.eq(Topic::getId, id);
        }
        if (!StringUtils.isEmpty(title)){
             query.eq(Topic::getTitle, title);
        }
        return Optional.ofNullable(topicMapper.selectOne(query));
    }

    @Override
    public List<Topic> listTopic(List<String> titles) {
        var query = new LambdaQueryWrapper<Topic>();
        query.in(Topic::getTitle, titles);
        return topicMapper.selectList(query);
    }

    @Override
    @Transactional
    public List<Topic> listOrCreateTopics(List<String> topicTitles){
        List<Topic> result = listTopic(new ArrayList<>(topicTitles));
        List<String> existedTopicTitles = result.stream().map(Topic::getTitle).collect(Collectors.toList());
        List<Topic> needCreateTopics = new ArrayList<>();
        topicTitles.stream().filter(item -> !existedTopicTitles.contains(item)).forEach(item -> {
            Topic topic = Topic.builder().searchKeyWord(StrUtil.format("{},{}", item, getFirstLetter(item))).effectValue(0)
                    .sourceType(TopicSourceTypeEnum.CUSTOMER_CUSTOMIZE.ordinal()).systemTopic(false).title(item).sourceId("0").build();
            topicMapper.insert(topic);
            result.add(topic);
            needCreateTopics.add(topic);
        });
        if (!needCreateTopics.isEmpty()){
            List<TopicEvent> topicChangedEventList = new ArrayList<>(needCreateTopics.size());
            needCreateTopics.forEach(item -> {
                TopicEvent topicChangedEvent = BeanUtil.prepare(item, TopicEvent.class);
                topicChangedEvent.setEventType(TopicEvent.EventTypeEnum.ADD.getValue());
                topicChangedEventList.add(topicChangedEvent);
            });
            kafkaTemplate.send(VideoEventTopic.TOPIC_NAME_TOPIC_CHANGED_BATCH, TopicEvent.EventTypeEnum.ADD.getValue().toString(), TopicBatchEvent.builder()
                    .topicEventList(topicChangedEventList).build());
        }
        final Map<String, Topic> topicResultMap = result.stream().collect(Collectors.toMap(item -> item.getTitle(), item -> item));
        List<Topic> orderedTopicResult = new ArrayList<>();
        topicTitles.forEach(item -> {
            orderedTopicResult.add(topicResultMap.get(item));
        });
        return result;
    }

    @Override
    public void favoriteTopic(Long customerId, Long topicId, Boolean favorite) {
        Optional<Topic> topic = getTopic(topicId, null);
        if (topic.isEmpty()){
            return;
        }
        Optional<TopicCustomerFavorite> topicCustomerFavorite = getTopicCustomerFavorite(customerId, topicId);
        if (topicCustomerFavorite.isPresent()){
            if (topicCustomerFavorite.get().getFavorite().equals(!favorite)){
                topicCustomerFavoriteMapper.updateById(TopicCustomerFavorite.builder().id(topicCustomerFavorite.get().getId()).favorite(favorite).build());
            }
            return;
        }
        topicCustomerFavoriteMapper.insert(TopicCustomerFavorite.builder()
                .favorite(favorite)
                .customerId(customerId)
                .topicSourceType(topic.get().getSourceType())
                .topicId(topicId)
                .build());
    }

    @Override
    public Boolean isFavoriteTopic(Long customerId, Long topicId) {
        if (customerId <= 0){
            return false;
        }
        Optional<TopicCustomerFavorite> topicCustomerFavorite = getTopicCustomerFavorite(customerId, topicId);
        if (topicCustomerFavorite.isPresent() && topicCustomerFavorite.get().getFavorite()){
            return true;
        }
        return false;
    }

    @Override
    public List<TopicCustomerFavorite> listTopicCustomerFavorite(Long customerId, TopicSourceTypeEnum topicSourceTypeEnum) {
        return topicCustomerFavoriteMapper.selectList(buildQuery(customerId, 0L, topicSourceTypeEnum == null ? null : topicSourceTypeEnum.ordinal(), true));
    }

    @Override
    public List<Topic> listTopic(ListTopicAO listTopicAO) {
        LambdaQueryWrapper query = buildQuery(listTopicAO.getIdList());
        return topicMapper.selectList(query);
    }

    @Override
    public PageList<Topic> listTopic(long customerId, PageDomain pageDomain) {
        LambdaQueryWrapper<TopicCustomerFavorite> query = buildQuery(customerId, 0L, null, true).orderByDesc(TopicCustomerFavorite::getId)
                .last(StrUtil.format(" limit {} ", pageDomain.getPageSize()));
        if (pageDomain.getCursor() > 0){
            query.lt(TopicCustomerFavorite::getId, pageDomain.getCursor());
        }
        List<TopicCustomerFavorite> topicCustomerFavorites = topicCustomerFavoriteMapper.selectList(query);
        if (topicCustomerFavorites.isEmpty()){
            return new PageList<>();
        }
        ListTopicAO listTopicAO = ListTopicAO.builder().idList(topicCustomerFavorites.stream().map(TopicCustomerFavorite::getTopicId).collect(Collectors.toList())).build();
        Map<Long, Topic> topics = listTopic(listTopicAO).stream().collect(Collectors.toMap(Topic::getId, item -> item));
        return PageHelper.buildPageList(pageDomain.getPageSize(), CollectionUtils.lastElement(topicCustomerFavorites).getId(), topicCustomerFavorites, (t) -> topics.get(t.getTopicId()));
    }

    private Optional<TopicCustomerFavorite> getTopicCustomerFavorite(Long customerId, Long topicId){
        return Optional.ofNullable(topicCustomerFavoriteMapper.selectOne(buildQuery(customerId, topicId, null, null)));
    }

    private LambdaQueryWrapper<TopicCustomerFavorite> buildQuery(long customerId, long topicId, Integer topicSourceType, Boolean favorite){
        LambdaQueryWrapper<TopicCustomerFavorite> query = new LambdaQueryWrapper<>();
        if (customerId >0){
            query = query.eq(TopicCustomerFavorite::getCustomerId, customerId);
        }
        if (topicId > 0){
            query = query.eq(TopicCustomerFavorite::getTopicId, topicId);
        }
        if (topicSourceType != null){
            query = query.eq(TopicCustomerFavorite::getTopicSourceType, topicSourceType);
        }
        if (favorite != null){
            query = query.eq(TopicCustomerFavorite::getFavorite, favorite);
        }
        return query;
    }

    private List listTopicSource(TopicSourceTypeEnum topicSourceType, Date afterDate, List<String> ids){
        var idList = ids.stream().map(item -> Long.parseLong(item)).collect(Collectors.toList());
        FinanceDataTypeEnum financeDataType = FINANCE_DATA_TYPE_MAP.get(topicSourceType);
        if (financeDataType == null){
            log.error("no finance data type find for topic  source type:{} ", topicSourceType);
            return Collections.EMPTY_LIST;
        }
        switch (financeDataType){
            case SECURITY:
                return financeRpcService.listSecurity(ListSecurityAO.builder().updateOnUtcAfter(afterDate).ids(idList).build());
            case INDUSTRY:
                return financeRpcService.listIndustry(ListIndustryAO.builder().updateOnUtcAfter(DateUtils.minDate()).ids(idList).build());
            default:
                log.error("no topic fill service find for topic source type:{}", financeDataType);
        }
        return Collections.EMPTY_LIST;
    }

    private <T>  void  initTopic(TopicSourceTypeEnum topicSourceType, List<T> source){
        if (source.isEmpty()){
            return;
        }
        Optional<TopicFillBaseService> topicFillService = SpringUtils.getBeansOfType(TopicFillBaseService.class).values().stream().filter(item -> item.getTopicSourceType() == topicSourceType).findFirst();
        if (topicFillService.isEmpty()){
            log.error("no topic fill service find for topic source type:{}", topicSourceType);
            return;
        }
        switch (topicSourceType){
            case SECURITY:
                topicFillService.get().initTopic(source);
                break;
            case INDUSTRY:
                topicFillService.get().initTopic(source);
                break;
            default:
                log.error("no topic fill service find for topic source type:{}", topicSourceType);
        }
    }

    private String getFirstLetter(String source){
        try {
            return ChineseCharToEn.getAllFirstLetter(source);
        }
        catch (UnsupportedEncodingException e){
            log.error(e.getMessage(), e);
            return source;
        }
    }

    private LambdaQueryWrapper buildQuery(List<Long> idList){
        LambdaQueryWrapper<Topic> query = new LambdaQueryWrapper<>();
        if (!CollectionUtils.isEmpty(idList)){
            query = query.in(Topic::getId, idList);
        }
        return query;
    }
}
