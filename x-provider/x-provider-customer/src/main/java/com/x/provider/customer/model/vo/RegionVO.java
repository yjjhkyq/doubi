package com.x.provider.customer.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionVO {
  @ApiModelProperty(value = "地区id")
  private Integer id;
  @ApiModelProperty(value = "父地区id")
  private Integer parentId;
  @ApiModelProperty(value = "地区层级 0 国家 1 省份 2 市区")
  private Integer level;
  @ApiModelProperty(value = "地区名称")
  private String name;
  @ApiModelProperty(value = "经度")
  private Double lng;
  @ApiModelProperty(value = "纬度")
  private Double lat;
  @ApiModelProperty(value = "所属国家id")
  private Integer countryId;
}
