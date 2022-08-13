package com.x.provider.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.pay.model.domain.asset.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionMapper extends BaseMapper<Transaction> {
}
