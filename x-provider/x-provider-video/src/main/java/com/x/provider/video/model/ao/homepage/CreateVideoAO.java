package com.x.provider.video.model.ao.homepage;

import com.x.provider.video.model.ao.ProductTitleItemAO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel
@Data
public class CreateVideoAO {

    @ApiModelProperty(value = "视频标题")
    @NotBlank
    private String title;

    @ApiModelProperty(value = "视频标文件 id")
    @NotBlank
    private String fileId;

    @ApiModelProperty(value = "视频标题项，多个项共同组成视频标题，例如 苹果市值持续走低 #苹果 #财经  此值应该为三项")
    private List<ProductTitleItemAO> productTitleItemList;

}
