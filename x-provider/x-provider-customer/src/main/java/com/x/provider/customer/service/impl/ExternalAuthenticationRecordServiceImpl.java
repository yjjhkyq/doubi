package com.x.provider.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.provider.customer.mapper.ExternalAuthenticationRecordMapper;
import com.x.provider.customer.model.domain.ExternalAuthenticationRecord;
import com.x.provider.customer.service.ExternalAuthenticationRecordService;
import com.x.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ExternalAuthenticationRecordServiceImpl implements ExternalAuthenticationRecordService {

    private final ExternalAuthenticationRecordMapper externalAuthenticationRecordMapper;

    public ExternalAuthenticationRecordServiceImpl(ExternalAuthenticationRecordMapper externalAuthenticationRecordMapper){
        this.externalAuthenticationRecordMapper = externalAuthenticationRecordMapper;
    }

    @Override
    public ExternalAuthenticationRecord get(Long id, Integer provider, String externalIdentifier, String unionExternalIdentifier) {
        LambdaQueryWrapper query = build(id, provider, externalIdentifier, unionExternalIdentifier);
        return externalAuthenticationRecordMapper.selectOne(query);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insert(ExternalAuthenticationRecord externalAuthenticationRecord) {
        externalAuthenticationRecordMapper.insert(externalAuthenticationRecord);
    }

    private LambdaQueryWrapper build(Long id, Integer provider, String externalIdentifier, String unionExternalIdentifier){
        LambdaQueryWrapper<ExternalAuthenticationRecord> query = new LambdaQueryWrapper<>();
        if (id != null && id > 0){
            query.eq(ExternalAuthenticationRecord::getId, id);
        }
        if (provider != null && provider >0){
            query.eq(ExternalAuthenticationRecord::getProvider, provider);
        }
        if (!StringUtils.isEmpty(externalIdentifier)){
            query.eq(ExternalAuthenticationRecord::getExternalIdentifier, externalIdentifier);
        }
        if (!StringUtils.isEmpty(unionExternalIdentifier)){
            query.eq(ExternalAuthenticationRecord::getUnionExternalIdentifier, unionExternalIdentifier);
        }
        return query;
    }
}
