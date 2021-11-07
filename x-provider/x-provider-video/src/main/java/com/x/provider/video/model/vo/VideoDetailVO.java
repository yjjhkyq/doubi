package com.x.provider.video.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.x.provider.video.model.vo.StatisticVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@ApiModel(value = "视频详情")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDetailVO {
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
//    @ApiModelProperty(value = "视频播放地址")
//    private String playUrl;
    @ApiModelProperty(value = "视频播放文件id")
    private String fileId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdOnUtc;
    @ApiModelProperty(value = "作者id")
    private long authorId;
    @ApiModelProperty(value = "和作者关系，0 没有关系 1 关注 2 朋友")
    private int toAuthorRelation;
    @ApiModelProperty(value = "true 可关注 反之false ")
    private boolean myVideo;
    @ApiModelProperty(value = "作者昵称")
    private String authorNickName;
    @ApiModelProperty(value = "作者头像")
    private String authorAvatarUrl;
    @ApiModelProperty(value = "统计数据")
    private StatisticVO statistic;
}
