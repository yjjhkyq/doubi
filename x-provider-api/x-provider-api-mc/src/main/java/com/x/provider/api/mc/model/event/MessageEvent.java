package com.x.provider.api.mc.model.event;

import com.x.core.enums.IntegerEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent {

    private Long id;
    private Long fromCustomerId;
    private Long toCustomerId = 0L;
    private Long toGroupId = 0L;
    private String messageType;
    private String alertMsg;
    private String msgBody;
    private Integer messageClass;
    private Integer eventType;
    private Boolean onlineUserOnly;
    private Date createdOnUtc;

    public enum EventTypeEnum implements IntegerEnum {
        SEND(1),
        ;

        private Integer value;

        EventTypeEnum(Integer value){
            this.value = value;
        }

        @Override
        public Integer getValue() {
            return value;
        }
    }
}
