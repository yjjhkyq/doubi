package com.x.provider.video.model.vo.homepage;

import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import com.x.provider.video.model.vo.VideoStatisticVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoListVO {
    @ApiModelProperty(value = "视频id")
    private long id;
    @ApiModelProperty(value = "true 置顶 反之false")
    private boolean top;
    @ApiModelProperty(value = "视频封面")
    private String coverUrl;
    @ApiModelProperty(value = "视频状态 1 审核中 2 已发布 3 不适宜公开")
    private int videoStatus;
    @ApiModelProperty(value = "统计数据")
    private VideoStatisticVO statistic;
    @ApiModelProperty(value = "作者id")
    private long customerId;
    @ApiModelProperty(value = "作者信息")
    private SimpleCustomerDTO customer;
}
