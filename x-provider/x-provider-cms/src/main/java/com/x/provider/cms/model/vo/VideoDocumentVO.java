package com.x.provider.cms.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel
public class VideoDocumentVO {
    @Id
    private Long id;
    @ApiModelProperty(value =  "作品作者id")
    private Long customerId;
    @ApiModelProperty(value = "作品文件id")
    private String fileId;
    @ApiModelProperty(value = "视频封面")
    private String coverUrl;
    @ApiModelProperty(value = "视频标题")
    private String title;
    @ApiModelProperty(value = "时长")
    private double duration;
    @ApiModelProperty(value = "视频创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createdOnUtc;
    @ApiModelProperty(value = "作者昵称")
    private String nickname;
    @ApiModelProperty(value = "作者头像url")
    private String avatarUrl;
}
