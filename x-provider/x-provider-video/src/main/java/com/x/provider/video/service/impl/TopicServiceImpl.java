package com.x.provider.video.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.DateUtils;
import com.x.core.utils.SpringUtils;
import com.x.provider.api.finance.enums.FinanceDataTypeEnum;
import com.x.provider.api.finance.model.ao.ListIndustryAO;
import com.x.provider.api.finance.model.ao.ListSecurityAO;
import com.x.provider.api.finance.model.event.FinanceDataChangedEvent;
import com.x.provider.api.finance.service.FinanceRpcService;
import com.x.provider.api.video.enums.TopicSourceTypeEnum;
import com.x.provider.video.configure.ApplicationConfig;
import com.x.provider.video.mapper.TopicMapper;
import com.x.provider.video.model.domain.Topic;
import com.x.provider.video.service.TopicFillBaseService;
import com.x.provider.video.service.TopicService;
import com.x.util.ChineseCharToEn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.sql.Struct;
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

    public TopicServiceImpl(TopicMapper topicMapper,
                            FinanceRpcService financeRpcService,
                            ApplicationConfig applicationConfig){
        this.topicMapper = topicMapper;
        this.financeRpcService = financeRpcService;
        this.applicationConfig = applicationConfig;
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

    public List<Topic> listOrCreateTopics(List<String> topicTitles){
        List<Topic> result = listTopic(topicTitles);
        List<String> existedTopicTitles = result.stream().map(Topic::getTitle).collect(Collectors.toList());
        List<Topic> needCreateTopics = new ArrayList<>();
        topicTitles.stream().filter(item -> !existedTopicTitles.contains(item)).forEach(item -> {
            Topic topic = Topic.builder().searchKeyWord(StrUtil.format("{},{}", item, getFirstLetter(item))).effectValue(0)
                    .sourceType(TopicSourceTypeEnum.CUSTOMER_CUSTOMIZE.ordinal()).systemTopic(false).title(item).sourceId("0").build();
            topicMapper.insert(topic);
            result.add(topic);
        });
        return result;
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
}
