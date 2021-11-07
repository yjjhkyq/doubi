package com.x.provider.video.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class ReportVideoPlayMetricAO {
    @ApiModelProperty(value = "视频id")
    private long videoId;
    @ApiModelProperty(value = "播放时长，单位秒")
    private int playDuration;
}
