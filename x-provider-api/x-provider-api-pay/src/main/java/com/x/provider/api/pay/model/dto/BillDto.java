package com.x.provider.api.pay.model.dto;

import com.x.provider.api.pay.enums.BillStatus;
import com.x.provider.api.pay.enums.TradeType;

import java.math.BigDecimal;

public class BillDto {

    private long id;

    private String serialNumber;  // IdWorker.getTimeId();

    private long customerId;  // 用户id
    private long toCustomerId;  // 引发订单的目标id, 例如向xx转账

    private TradeType type;
    private BillStatus status;

    private BigDecimal amount;

    private String comment;  // 备注

    public BillDto() {
    }

    public BillDto(long id, String serialNumber, long customerId, long toCustomerId, TradeType type, BillStatus status, BigDecimal amount, String comment) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.customerId = customerId;
        this.toCustomerId = toCustomerId;
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getToCustomerId() {
        return toCustomerId;
    }

    public void setToCustomerId(long toCustomerId) {
        this.toCustomerId = toCustomerId;
    }

    public TradeType getType() {
        return type;
    }

    public void setType(TradeType type) {
        this.type = type;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
