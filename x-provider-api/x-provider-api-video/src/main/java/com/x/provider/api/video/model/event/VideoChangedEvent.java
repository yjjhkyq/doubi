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
public class VideoChangedEvent {
    private Integer eventType;
    private Long id;
    private Long customerId;
    private String fileId;
    private String title;
    private Boolean reviewed;
    private Integer videoStatus;
    private Boolean top;
    private Long topValue;
    private double duration;

    public enum EventTypeEnum implements IntegerEnum {
        VIDEO_PUBLISHED(1),
        VIDEO_DELETED(2),

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
