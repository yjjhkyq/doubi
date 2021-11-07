package com.x.provider.video.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {
    @ApiModelProperty(value = "视频id")
    private long id;
    @ApiModelProperty(value = "评论用户id")
    private long commentCustomerId;
    @ApiModelProperty(value = "评论用户昵称")
    private String commentCustomerNickName;
    @ApiModelProperty(value = "评论内容")
    private String content;
    @ApiModelProperty(value = "被回复用户id")
    private long replyCustomerId;
    @ApiModelProperty(value = "被回复用户昵称")
    private String replyCustomerNickName;
    @ApiModelProperty(value = "true 作者评论 反之false")
    private boolean author;
    @ApiModelProperty(value = "待显示评论回复数")
    private long replyNeedShowTotalCount;
    @ApiModelProperty(value = "评论点赞数")
    private long starCount;
    @ApiModelProperty(value = "作者回复评论")
    private List<CommentVO> authorReplyList;
}
