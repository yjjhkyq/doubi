package com.x.provider.mc.model.ao;

import com.x.core.web.page.PageDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadMessageAO extends PageDomain {
    @ApiModelProperty(value = "发送人用户id")
    private Long senderUid;
}
