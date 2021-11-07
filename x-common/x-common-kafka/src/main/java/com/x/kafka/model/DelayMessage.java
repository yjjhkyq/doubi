package com.x.kafka.model;

/**
 * 延迟消息
 * @author: liushenyi
 * @date: 2021/10/25/19:20
 */
public class DelayMessage {
    private String topic;
    private String key;
    private String value;
    private long createTimeMillis;
    private long delayTimeInMillis;
    private long reSendTimeMillis;

    public DelayMessage(){

    }

    public DelayMessage(String topic, String key, String value, long delayTimeInMillis){
        this.topic = topic;
        this.key = key;
        this.value = value;
        this.createTimeMillis = System.currentTimeMillis();
        this.delayTimeInMillis = delayTimeInMillis;
        this.reSendTimeMillis = System.currentTimeMillis() + delayTimeInMillis;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getCreateTimeMillis() {
        return createTimeMillis;
    }

    public void setCreateTimeMillis(long createTimeMillis) {
        this.createTimeMillis = createTimeMillis;
    }

    public long getDelayTimeInMillis() {
        return delayTimeInMillis;
    }

    public void setDelayTimeInMillis(long delayTimeInMillis) {
        this.delayTimeInMillis = delayTimeInMillis;
    }

    public long getReSendTimeMillis() {
        return reSendTimeMillis;
    }

    public void setReSendTimeMillis(long reSendTimeMillis) {
        this.reSendTimeMillis = reSendTimeMillis;
    }
}
