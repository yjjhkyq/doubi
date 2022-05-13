package com.x.provider.video.model.vo.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SecurityVO {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "股票编码")
    private String code;

    @ApiModelProperty(value = "股票代码")
    private String symbol;

    @ApiModelProperty(value = "股票中文简称")
    private String name;

    @ApiModelProperty(value = "股票全称")
    private String fullName;

    @ApiModelProperty(value = "交易所代码 SSE 上交所 SZSE深交所")
    private String exchange;
}
