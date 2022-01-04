package com.x.provider.general.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class CommentStatisticVO {
    private Long id;
    @ApiModelProperty("评论点赞数")
    private long starCount;
    @ApiModelProperty("评论回复数")
    private long replyCount;
}
