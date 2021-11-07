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
public class VideoCommentReplyAO {
    @ApiModelProperty(value = "评论id")
    private long commentId;
    @ApiModelProperty(value = "评论内容")
    private String content;
}
