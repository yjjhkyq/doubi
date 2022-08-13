package com.x.provider.customer.model.vo;

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
public class GenericAttributeVO {
  private Long id;
  private Long entityId;
  @ApiModelProperty(value = "属性分组")
  private String keyGroup;
  @ApiModelProperty(value = "属性key")
  private String key;
  @ApiModelProperty(value = "属性值")
  private String value;
  @ApiModelProperty(value = "审核结果 1 审核通过 2 拒绝 3 审核中")
  private Integer suggestionType;
  @ApiModelProperty(value = "属性值对应的访问url,当值为图片、视频类型，需要通过url访问时，此属性有值")
  private String valueUrl;
}
