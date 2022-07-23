package com.x.provider.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.BeanUtil;
import com.x.provider.api.finance.constants.FinanceEventTopic;
import com.x.provider.api.finance.enums.FinanceDataTypeEnum;
import com.x.provider.api.finance.model.ao.ListIndustryAO;
import com.x.provider.api.finance.model.ao.ListSecurityAO;
import com.x.provider.api.finance.model.event.FinanceDataChangedEvent;
import com.x.provider.api.finance.model.event.FinanceDataChangedEventEnum;
import com.x.provider.api.finance.model.event.SecurityChangedBatchEvent;
import com.x.provider.api.finance.model.event.SecurityChangedEvent;
import com.x.provider.finance.mapper.IndustryMapper;
import com.x.provider.finance.mapper.SecurityMapper;
import com.x.provider.finance.model.domain.Industry;
import com.x.provider.finance.model.domain.Security;
import com.x.provider.finance.service.DataProducer;
import com.x.provider.finance.service.FinanceDataService;
import com.x.util.ChineseCharToEnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FinanceDataServiceImpl implements FinanceDataService {

    private final DataProducer dataProducer;
    private final SecurityMapper securityMapper;
    private final IndustryMapper industryMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    public FinanceDataServiceImpl(DataProducer dataProducer,
                                  SecurityMapper securityMapper,
                                  IndustryMapper industryMapper,
                                   KafkaTemplate<String, Object> kafkaTemplate){
        this.dataProducer = dataProducer;
        this.securityMapper = securityMapper;
        this.industryMapper = industryMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void syncSecurity() {
        Map<String, Security> securityMap = dataProducer.produceStock().stream().collect(Collectors.toMap(Security::getCode, item -> item));
        Map<String, Security> securityMapExisted = securityMapper.selectList(new LambdaQueryWrapper<>()).stream().collect(Collectors.toMap(Security::getCode, item -> item));
        List<Security> updateSecurity = new ArrayList<>(securityMap.size());
        List<Security> addSecurity = new ArrayList<>(securityMap.size());
        securityMap.entrySet().forEach(item -> {
            if (securityMapExisted.containsKey(item.getKey()) && !equal(item.getValue(), securityMapExisted.get(item.getKey()))){
                Security security = securityMapExisted.get(item.getKey());
                security.setName(item.getValue().getName());
                security.setCnSpell(item.getValue().getCnSpell());
                security.setSymbol(item.getValue().getSymbol());
                updateSecurity.add(security);
            }
            else {
                addSecurity.add(item.getValue());
            }
        });
        updateSecurity.forEach(item -> {
            securityMapper.updateById(item);
        });
        addSecurity.forEach(item -> {
            securityMapper.insert(item);
        });
//        updateSecurity.add(Security.builder().id(1L).build());
//        addSecurity.add(Security.builder().id(2L).build());

        List<SecurityChangedEvent> securityChangedEventList = new ArrayList<>(updateSecurity.size() + addSecurity.size());
        if (!updateSecurity.isEmpty()){
            updateSecurity.forEach(item -> {
                SecurityChangedEvent securityChangedEvent = BeanUtil.prepare(item, SecurityChangedEvent.class);
                securityChangedEvent.setFinanceDataChangedEventEnum(FinanceDataChangedEventEnum.UPDATE);
                securityChangedEventList.add(securityChangedEvent);
            });
            kafkaTemplate.send(FinanceDataChangedEventEnum.TOPIC_NAME, FinanceDataTypeEnum.SECURITY.name(), FinanceDataChangedEvent.builder().financeDataChangedEventEnum(FinanceDataChangedEventEnum.UPDATE)
                    .financeDataType(FinanceDataTypeEnum.SECURITY).ids(updateSecurity.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.toList())).build());
        }
        if (!addSecurity.isEmpty()){
            addSecurity.forEach(item -> {
                SecurityChangedEvent securityChangedEvent = BeanUtil.prepare(item, SecurityChangedEvent.class);
                securityChangedEvent.setFinanceDataChangedEventEnum(FinanceDataChangedEventEnum.ADD);
                securityChangedEventList.add(securityChangedEvent);
            });
            kafkaTemplate.send(FinanceDataChangedEventEnum.TOPIC_NAME, FinanceDataTypeEnum.SECURITY.name(), FinanceDataChangedEvent.builder().financeDataChangedEventEnum(FinanceDataChangedEventEnum.ADD)
                    .financeDataType(FinanceDataTypeEnum.SECURITY).ids(updateSecurity.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.toList())).build());
        }
        if (securityChangedEventList.size() > 0){
            kafkaTemplate.send(FinanceEventTopic.TOPIC_NAME_SECURITY_BATCH_CHANGED, FinanceDataTypeEnum.SECURITY.name(),
                    SecurityChangedBatchEvent.builder().securityChangedEventList(securityChangedEventList).build());
        }
    }

    @Override
    public void  fillIndustryCnSpell(){
        List<Industry> industries = industryMapper.selectList(new LambdaQueryWrapper<>());
        List<Industry> needFillItems = industries.stream().filter(item -> StringUtils.isEmpty(item.getCnSpell())).collect(Collectors.toList());
        needFillItems.forEach(item -> {
            try {
                item.setCnSpell(ChineseCharToEnUtils.getAllFirstLetter(item.getName()));
                industryMapper.updateById(item);
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public List<Industry> listIndustry(List<Long> ids, Date updateOnUtcAfter) {
        var query = new LambdaQueryWrapper<Industry>();
        if (!CollectionUtils.isEmpty(ids)){
            query.in(Industry::getId, ids);
        }
        if (updateOnUtcAfter != null){
            query.ge(Industry::getUpdatedOnUtc, updateOnUtcAfter);
        }
        return industryMapper.selectList(query);
    }

    @Override
    public List<Security> listSecurity(List<Long> ids, Date updateOnUtcAfter) {
        var query = new LambdaQueryWrapper<Security>();
        if (!CollectionUtils.isEmpty(ids)){
            query.in(Security::getId, ids);
        }
        if (updateOnUtcAfter != null){
            query.ge(Security::getUpdatedOnUtc, updateOnUtcAfter);
        }
        return securityMapper.selectList(query);
    }

    @Override
    public List<Security> listSecurity(ListSecurityAO listSecurityAO) {
        return listSecurity(listSecurityAO.getIds(), listSecurityAO.getUpdateOnUtcAfter());
    }

    @Override
    public List<Industry> listIndustry(ListIndustryAO listIndustryAO) {
        return listIndustry(listIndustryAO.getIds(), listIndustryAO.getUpdateOnUtcAfter());
    }

    private boolean equal(Security s1, Security s2){
        return Objects.equals(s1.getCnSpell(), s2.getCnSpell()) && Objects.equals(s1.getCode(), s2.getCode()) && Objects.equals(s1.getEnName(), s2.getEnName())
                && Objects.equals(s1.getExchange(), s2.getExchange()) && Objects.equals(s1.getFullName(), s2.getFullName()) && Objects.equals(s1.getName(), s2.getType())
                && Objects.equals(s1.getParentCode(), s2.getParentCode()) && Objects.equals(s1.getSymbol(), s2.getSymbol()) && Objects.equals(s1.getType(), s2.getType());
    }
}
