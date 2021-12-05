package com.x.provider.api.video.model.event;

import com.x.core.enums.IntegerEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicEvent {
    private Integer eventType;
    private Long id;
    private String title;
    private Integer effectValue;
    private Integer sourceType;
    private String searchKeyWord;
    private String sourceId;
    private String topicDescription;
    private Boolean systemTopic;

    public enum EventTypeEnum implements IntegerEnum {
        ADD(1),
        DELETED(2),
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
