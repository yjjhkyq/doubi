package com.x.provider.cms.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "所有话题都需要显示title systemTopic属性，而对于股票话题，还需要显示：securityDocument.symbol securityDocument.exchange")
public class TopicDocumentVO {
    @ApiModelProperty(value = "话题id")
    private Long id;
    @ApiModelProperty(value = "话题标题")
    private String title;
    private Integer effectValue;
    @ApiModelProperty(value = "话题类型，0 股票 1 行业 2 用户自定义话题")
    private Integer sourceType;
    private String sourceId;
    @ApiModelProperty(value = "话题描述信息，有可能没有值")
    private String topicDescription;
    @ApiModelProperty(value = "true 系统话题 反之 false ,单位系统话题时，应该在话题列表中高亮显示 官方 两个字")
    private Boolean systemTopic;

    @ApiModelProperty(value = "股票，当话题类型为0时，此属性有值")
    private SecurityDocumentVO securityDocument;
}
