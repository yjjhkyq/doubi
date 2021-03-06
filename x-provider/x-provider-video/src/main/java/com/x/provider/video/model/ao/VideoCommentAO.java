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
    @ApiModelProperty(value = "当时回复评论时，填入要回复的评论id,否则填入0")
    private long parentCommentId;
}
