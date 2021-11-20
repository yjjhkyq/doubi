package com.x.provider.finance.service.impl;

import com.x.provider.api.finance.enums.SecurityTypeEnum;
import com.x.provider.finance.model.domain.Security;
import com.x.provider.finance.service.DataProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataProducerImpl implements DataProducer {

    private final Tushare tushare;

    public DataProducerImpl(Tushare tushare) {
        this.tushare = tushare;
    }

    @Override
    public List<Security> produceStock() {
        List<Map> stocks = tushare.stockBasic();
        List<Security> result = new ArrayList<>(stocks.size());
        stocks.stream().forEach(item -> {
            result.add(Security.builder()
                .code(String.valueOf(item.get("ts_code")))
                .symbol(String.valueOf(item.get("symbol")))
                .name(String.valueOf(item.get("name")))
                .cnSpell(String.valueOf(item.get("cnspell")))
                .fullName(String.valueOf(item.get("fullname")))
                .enName(String.valueOf(item.get("enname")))
                .exchange(String.valueOf(item.get("exchange")))
                .type(SecurityTypeEnum.STOCK.name())
                .build());
        });
        return result;
    }
}
