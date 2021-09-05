package com.x.provider.api.finance.model.event;

public enum FinanceDataChangedEventEnum {
    ADD,
    UPDATE,
    DELETE;
    public static final String TOPIC_NAME = "finance-data-changed";
}
