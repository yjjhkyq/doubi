package com.x.provider.cms.service.impl;

import com.x.core.utils.BeanUtil;
import com.x.core.utils.JsonUtil;
import com.x.provider.api.customer.model.event.CustomerEvent;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.finance.model.ao.ListSecurityAO;
import com.x.provider.api.finance.model.dto.SecurityDTO;
import com.x.provider.api.finance.model.event.SecurityChangedBatchEvent;
import com.x.provider.api.finance.service.FinanceRpcService;
import com.x.provider.api.video.enums.TopicSourceTypeEnum;
import com.x.provider.api.video.model.ao.ListTopicAO;
import com.x.provider.api.video.model.dto.TopicDTO;
import com.x.provider.api.video.model.event.TopicBatchEvent;
import com.x.provider.api.video.model.event.TopicEvent;
import com.x.provider.api.video.model.event.VideoChangedEvent;
import com.x.provider.api.video.service.VideoRpcService;
import com.x.provider.api.vod.service.VodRpcService;
import com.x.provider.cms.model.domain.CustomerDocument;
import com.x.provider.cms.model.domain.SecurityDocument;
import com.x.provider.cms.model.domain.TopicDocument;
import com.x.provider.cms.model.domain.VideoDocument;
import com.x.provider.cms.repository.CustomerDocumentRepository;
import com.x.provider.cms.repository.SecurityDocumentRepository;
import com.x.provider.cms.repository.TopicDocumentRepository;
import com.x.provider.cms.repository.VideoDocumentRepository;
import com.x.provider.cms.service.SearchService;
import com.x.util.ChineseCharToEn;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    private final SecurityDocumentRepository securityDocumentRepository;
    private final FinanceRpcService financeRpcService;
    private final ElasticsearchRestTemplate esRestTemplate;
    private final TopicDocumentRepository topicDocumentRepository;
    private final VideoRpcService videoRpcService;
    private final VideoDocumentRepository videoDocumentRepository;
    private final VodRpcService vodRpcService;
    private final CustomerDocumentRepository customerDocumentRepository;
    private final CustomerRpcService customerRpcService;

    public SearchServiceImpl(SecurityDocumentRepository securityDocumentRepository,
                             FinanceRpcService financeRpcService,
                             ElasticsearchRestTemplate esRestTemplate,
                             TopicDocumentRepository topicDocumentRepository,
                             VideoRpcService videoRpcService,
                             VideoDocumentRepository videoDocumentRepository,
                             VodRpcService vodRpcService,
                             CustomerDocumentRepository customerDocumentRepository,
                             CustomerRpcService customerRpcService){
        this.securityDocumentRepository = securityDocumentRepository;
        this.financeRpcService = financeRpcService;
        this.esRestTemplate = esRestTemplate;
        this.topicDocumentRepository = topicDocumentRepository;
        this.videoRpcService = videoRpcService;
        this.videoDocumentRepository = videoDocumentRepository;
        this.vodRpcService = vodRpcService;
        this.customerDocumentRepository = customerDocumentRepository;
        this.customerRpcService = customerRpcService;
    }

    @Override
    public Page<SecurityDocument> searchSecurity(String keyword, Pageable page) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("symbol", "*" + keyword +"*"));
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("cnSpell", "*" + keyword +"*"));
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("name", "*" + keyword +"*"));
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("fullName", "*" + keyword +"*"));
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .build();
        esRestTemplate.queryForPage(searchQuery, SecurityDocument.class);
        return securityDocumentRepository.search(boolQueryBuilder, page);
    }

    @Override
    public Page<TopicDocument> searchTopic(String keyword, Pageable pageable) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("keyword1", "*" + keyword +"*"));
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("keyword2", "*" + keyword +"*"));
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("keyword3", "*" + keyword +"*"));
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("titleCnSpell", "*" + keyword +"*"));
        boolQueryBuilder.should(QueryBuilders.matchQuery("searchKeyWord", keyword));
        boolQueryBuilder.should(QueryBuilders.matchQuery("topicDescription", keyword));
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withSort(SortBuilders.fieldSort("effectValue").order(SortOrder.DESC))
                .build();
        esRestTemplate.queryForPage(searchQuery, TopicDocument.class);
        return topicDocumentRepository.search(boolQueryBuilder, pageable);
    }

    @Override
    public Page<VideoDocument> searchVideo(String keyword, Pageable pageable) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("title", "*" + keyword +"*"));
        boolQueryBuilder.should(QueryBuilders.matchQuery("title", keyword));
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withSort(SortBuilders.fieldSort("createdOnUtc").order(SortOrder.DESC))
                .build();
        esRestTemplate.queryForPage(searchQuery, VideoDocument.class);
        return videoDocumentRepository.search(boolQueryBuilder, pageable);
    }

    @Override
    public Page<CustomerDocument> searchCustomer(String keyword, Pageable pageable) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("userName", "*" + keyword +"*"));
        boolQueryBuilder.should(QueryBuilders.matchQuery("nickName", keyword));
        boolQueryBuilder.should(QueryBuilders.matchQuery("signature", keyword));
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .build();
        esRestTemplate.queryForPage(searchQuery, CustomerDocument.class);
        return customerDocumentRepository.search(boolQueryBuilder, pageable);
    }

    @Override
    public void initSecurityList(){
        List<SecurityDTO> securityList = financeRpcService.listSecurity(ListSecurityAO.builder().build());
        addOrUpdateSecurity(securityList);
    }

    @Override
    public void onSecurityChanged(SecurityChangedBatchEvent event) {
        List<SecurityDocument> securityDocuments = BeanUtil.prepare(event.getSecurityChangedEventList(), SecurityDocument.class);
        securityDocumentRepository.saveAll(securityDocuments);
    }

    @Override
    public void onTopicBatchChangedEvent(TopicBatchEvent event) {
        List<TopicDocument> topicDocumentList = BeanUtil.prepare( event.getTopicEventList().stream().filter(item -> !item.getEventType().equals(TopicEvent.EventTypeEnum.DELETED.getValue()))
                .collect(Collectors.toList()), TopicDocument.class);
        if (!topicDocumentList.isEmpty()) {
            topicDocumentRepository.saveAll(topicDocumentList);
        }
    }

    @Override
    public void initTopicList() {
        List<TopicDTO> topicList = videoRpcService.listTopic(ListTopicAO.builder().build()).getData();
        List<TopicDocument> topicDocumentList = BeanUtil.prepare(topicList, TopicDocument.class);
        prepare(topicDocumentList);
        topicDocumentRepository.saveAll(topicDocumentList);

    }

    @Override
    public void onVideoChanged(VideoChangedEvent videoChangedEvent) {
        log.info("video changed, data:{}", JsonUtil.toJSONString(videoChangedEvent));
        if (videoChangedEvent.getEventType().equals(VideoChangedEvent.EventTypeEnum.VIDEO_PUBLISHED.getValue())){
            VideoDocument videoDocument = BeanUtil.prepare(videoChangedEvent, VideoDocument.class);
            videoDocument.setCoverUrl(vodRpcService.getMediaInfo(videoChangedEvent.getFileId()).getData().getCoverUrl());
            videoDocumentRepository.save(videoDocument);
        }
        if (videoChangedEvent.getEventType().equals(VideoChangedEvent.EventTypeEnum.VIDEO_DELETED.getValue())){
            videoDocumentRepository.deleteById(videoChangedEvent.getId());
        }
    }

    @Override
    public void onCustomerInfoChanged(CustomerEvent customerEvent) {
        if (Arrays.asList(CustomerEvent.EventTypeEnum.ADD.getValue(), CustomerEvent.EventTypeEnum.UPDATE.getValue()).contains(customerEvent.getEventType()) && customerEvent.isRegisterRole()){
            CustomerDocument customerDocument = customerEvent.getEventType().equals(CustomerEvent.EventTypeEnum.UPDATE.getValue())
                    ? customerDocumentRepository.findById(customerEvent.getId()).orElse(new CustomerDocument()) : new CustomerDocument();
            customerDocument.setId(customerEvent.getId());
            if (!StringUtils.isEmpty(customerEvent.getUserName())){
                customerDocument.setUserName(customerEvent.getUserName());
            }
            if (customerEvent.getCreatedOnUtc() != null){
                customerDocument.setCreatedOnUtc(customerEvent.getCreatedOnUtc());
            }
            if (customerEvent.getCustomerAttributeEvent() != null){
                if (!StringUtils.isEmpty(customerEvent.getCustomerAttributeEvent().getAvatarId())){
                    customerDocument.setAvatarId(customerEvent.getCustomerAttributeEvent().getAvatarId());
                }
                if (!StringUtils.isEmpty(customerEvent.getCustomerAttributeEvent().getAvatarUrl())){
                    customerDocument.setAvatarUrl(customerEvent.getCustomerAttributeEvent().getAvatarUrl());
                }
                if (!StringUtils.isEmpty(customerEvent.getCustomerAttributeEvent().getNickName())){
                    customerDocument.setNickName(customerEvent.getCustomerAttributeEvent().getNickName());
                }
                if (!StringUtils.isEmpty(customerEvent.getCustomerAttributeEvent().getSignature())){
                    customerDocument.setSignature(customerEvent.getCustomerAttributeEvent().getSignature());
                }
            }
            customerDocumentRepository.save(customerDocument);
        }
    }

    public void addOrUpdateSecurity(List<SecurityDTO> securityList){
        List<SecurityDocument> securityDocuments = BeanUtil.prepare(securityList, SecurityDocument.class);
        securityDocumentRepository.saveAll(securityDocuments);
    }

    public void prepare(List<TopicDocument> topicDocumentList){
        Set<Long> securityIdList = topicDocumentList.stream().filter(item -> item.getSourceType().equals(TopicSourceTypeEnum.SECURITY.ordinal())).map(TopicDocument::getSourceId).filter(item -> StringUtils.hasText(item))
                .map(Long::valueOf).collect(Collectors.toSet());
        Map<Long, SecurityDTO> securityMap = financeRpcService.listSecurity(ListSecurityAO.builder().ids(new ArrayList<>(securityIdList)).build()).stream().collect(Collectors.toMap(SecurityDTO::getId, item -> item));
        topicDocumentList.forEach(item -> {
            try {
                item.setTitleCnSpell(ChineseCharToEn.getAllFirstLetter(item.getTitle()));
            } catch (UnsupportedEncodingException e) {
                log.error("get first letter error, id:{} title:{}", item.getId(), item.getTitle());
            }
            if (item.getSourceType().equals(TopicSourceTypeEnum.SECURITY.ordinal())){
                SecurityDTO security = securityMap.get(Long.parseLong(item.getSourceId()));
                if (security == null){
                    return;
                }
                item.setKeyword1(security.getCode());
                item.setKeyword2(security.getName());
                item.setKeyword3(security.getCnSpell());
            }
            if (item.getSourceType().equals(TopicSourceTypeEnum.INDUSTRY.ordinal())){
                SecurityDTO security = securityMap.get(Long.parseLong(item.getSourceId()));
                if (security == null){
                    return;
                }
                item.setKeyword1(security.getCode());
                item.setKeyword2(security.getName());
                item.setKeyword3(security.getCnSpell());
            }
        });
    }

}
