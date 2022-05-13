package com.x.provider.video.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "互动信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractVO {
    private boolean stared;
}
