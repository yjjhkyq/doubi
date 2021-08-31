package com.x.provider.finance.service;

import com.x.provider.finance.model.domain.Security;

import java.util.List;

public interface DataProducer {
    List<Security> produceStock();
}
