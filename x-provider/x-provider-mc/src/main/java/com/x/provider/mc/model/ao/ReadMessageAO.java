package com.x.provider.mc.model.ao;

import com.x.core.web.page.CursorPageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadMessageAO extends CursorPageRequest {
    @ApiModelProperty(value = "发送人用户id")
    private Long senderUid;
}
