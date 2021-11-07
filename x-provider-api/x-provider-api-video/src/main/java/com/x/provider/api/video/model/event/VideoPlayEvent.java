package com.x.provider.api.video.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoPlayEvent {
    private Long id;
    private Long customerId;
    private String fileId;
    private String title;
    private Boolean reviewed;
    private Integer videoStatus;
    private Boolean top;
    private Long topValue;
    private double duration;
    private int playDuration;
}
