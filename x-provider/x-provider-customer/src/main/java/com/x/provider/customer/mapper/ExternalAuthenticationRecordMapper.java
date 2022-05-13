package com.x.provider.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.customer.model.domain.Customer;
import com.x.provider.customer.model.domain.ExternalAuthenticationRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalAuthenticationRecordMapper extends BaseMapper<ExternalAuthenticationRecord> {
}
