package com.x.provider.video.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@ApiModel(value = "视频信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoVO {
    @ApiModelProperty(value = "视频id")
    private Long id;
    @ApiModelProperty(value = "视频标题")
    private String title;
    @ApiModelProperty(value = "视频封面")
    private String coverUrl;
    @ApiModelProperty(value = "true 置顶， 反之false")
    private boolean top;
    @ApiModelProperty(value = "视频状态 1 审核中 2 已发布 3 不适宜公开")
    private int videoStatus;
    @ApiModelProperty(value = "视频播放文件id")
    private String fileId;
    @ApiModelProperty(value = "文件播放地址")
    private String vodUrl;
    @ApiModelProperty(value = "创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdOnUtc;
    @ApiModelProperty(value = "作者id")
    private long customerId;
    @ApiModelProperty(value = "作者信息")
    private SimpleCustomerDTO customer;
    @ApiModelProperty(value = "统计数据")
    private VideoStatisticVO statistic;
    @ApiModelProperty(value = "播放时长")
    private double duration;
    @ApiModelProperty(value = "作品标题项")
    private List<ProductTitleItemVO> productTitleItemList;
    @ApiModelProperty(value = "作品交互")
    private InteractVO interact = new InteractVO();


}
