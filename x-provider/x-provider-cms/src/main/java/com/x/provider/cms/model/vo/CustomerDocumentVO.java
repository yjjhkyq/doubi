package com.x.provider.cms.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
@Data
@ApiModel
public class CustomerDocumentVO {
    @ApiModelProperty(value = "用户id")
    private Long id;
    @ApiModelProperty(value = "用户名，类似于抖音号")
    private String userName;
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "签名")
    private String signature;
    @ApiModelProperty(value = "用户头像id")
    private String avatarId;
    @ApiModelProperty(value = "用户头像url")
    private String avatarUrl;
    private Date createdOnUtc;
}
