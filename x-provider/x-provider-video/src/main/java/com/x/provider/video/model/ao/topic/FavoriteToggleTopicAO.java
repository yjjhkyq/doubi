package com.x.provider.video.model.ao.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: liushenyi
 * @date: 2021/11/03/19:10
 */
@ApiModel
@Data
public class FavoriteToggleTopicAO {
    @ApiModelProperty(value = "主题id")
    private long id;

    @ApiModelProperty(value = "true 自选 反之false")
    private boolean favorite;
}
