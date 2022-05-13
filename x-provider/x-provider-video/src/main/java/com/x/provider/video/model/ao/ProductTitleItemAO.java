package com.x.provider.video.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: liushenyi
 * @date: 2022/04/11/11:13
 */
@Data
@ApiModel
public class ProductTitleItemAO {
    @ApiModelProperty(value = "作品标题类型, 1 文本 2 话题 2 at")
    private int videoTitleType;
    @ApiModelProperty(value = "作品标题")
    private String text;
    @ApiModelProperty(value = "作品标题id")
    private Long key;
}
