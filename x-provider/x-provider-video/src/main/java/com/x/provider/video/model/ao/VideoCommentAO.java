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
public class VideoCommentAO {
    @ApiModelProperty(value = "视频id")
    private long itemId;
    @ApiModelProperty(value = "评论内容")
    private String content;
}
