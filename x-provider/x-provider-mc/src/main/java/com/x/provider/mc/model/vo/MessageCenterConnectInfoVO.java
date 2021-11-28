package com.x.provider.mc.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageCenterConnectInfoVO {
    @ApiModelProperty(value = "web socket 地址")
    private String webSocketUrl;
    @ApiModelProperty(value = "访问token")
    private String authenticationToken;
    @ApiModelProperty(value = "需要订阅的频道列表")
    private List<String> subscribeChannelList;
}
