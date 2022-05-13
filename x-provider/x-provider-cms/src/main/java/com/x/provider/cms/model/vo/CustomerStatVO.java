package com.x.provider.cms.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class CustomerStatVO {
    private Long id;
    @ApiModelProperty(value = "关注数")
    private Long followCount = 0L;
    @ApiModelProperty(value = "粉丝数")
    private Long fansCount = 0L;
    @ApiModelProperty(value = "点赞数")
    private Long starCount = 0L;
}
