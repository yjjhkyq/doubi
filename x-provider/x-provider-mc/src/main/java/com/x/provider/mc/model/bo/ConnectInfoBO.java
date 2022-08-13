package com.x.provider.mc.model.bo;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectInfoBO {
    private String webSocketUrl;
    private String authenticationToken;
    private List<String> subscribeChannelList;
    private Integer webSocketEngineType;
}
