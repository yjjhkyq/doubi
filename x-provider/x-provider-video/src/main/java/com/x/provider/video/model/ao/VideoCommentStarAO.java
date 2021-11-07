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
public class VideoCommentStarAO {
    @ApiModelProperty(value = "视频id")
    private long videoId;
    @ApiModelProperty(value = "评论id")
    private long commentId;
    @ApiModelProperty(value = "true 点赞 反之false")
    private boolean star;
}
