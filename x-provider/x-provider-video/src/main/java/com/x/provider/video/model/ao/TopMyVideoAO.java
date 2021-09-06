package com.x.provider.video.model.ao;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TopMyVideoAO {
    @Min(1)
    private long videoId;
    private boolean top;
}
