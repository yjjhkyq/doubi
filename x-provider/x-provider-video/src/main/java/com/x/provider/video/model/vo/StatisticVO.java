package com.x.provider.video.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "视频统计数据")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticVO {
    @ApiModelProperty(value = "评论数")
    private long commentCount;
    @ApiModelProperty(value = "点赞数")
    private long starCount;
    @ApiModelProperty(value = "播放数，只有作者本人可见")
    private long playCount;
    @ApiModelProperty(value = "转发数")
    private long forwardCount;
    @ApiModelProperty(value = "分享数")
    private long shareCount;
}
