package com.x.provider.video.model.vo.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: liushenyi
 * @date: 2021/11/03/19:17
 */
@ApiModel(description = "所有话题都需要显示title systemTopic属性，而对于股票话题，还需要显示：security.symbol security.exchange")
@Data
@NoArgsConstructor
public class TopicVO {

    @ApiModelProperty(value = "主题id")
    private long id;

    @ApiModelProperty(value = "主题类型, 0 股票 1 行业 2 用户自定义")
    private int sourceType;

    @ApiModelProperty(value = "主题标题")
    private String title;

    @ApiModelProperty(value = "主题编码")
    private String code;

    @ApiModelProperty(value = "true 我已加入自选 反之false")
    private boolean myFavorite;

    @ApiModelProperty(value = "股票")
    private SecurityVO security;
}
