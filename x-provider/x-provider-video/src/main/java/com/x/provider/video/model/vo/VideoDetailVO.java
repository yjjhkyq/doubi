package com.x.provider.video.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "视频详情")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDetailVO {
    @ApiModelProperty(value = "视频id")
    private Long id;
    @ApiModelProperty(value = "视频标题")
    private String title;
    @ApiModelProperty(value = "视频封面")
    private String coverUrl;
    @ApiModelProperty(value = "true 置顶， 反之false")
    private boolean top;
    @ApiModelProperty(value = "视频状态 1 审核中 2 已发布 3 不适宜公开")
    private int videoStatus;
    @ApiModelProperty(value = "视频播放地址")
    private String playUrl;
    @ApiModelProperty(value = "作者id")
    private long customerId;
    @ApiModelProperty(value = "统计数据")
    private StatisticVO statistic;
}
