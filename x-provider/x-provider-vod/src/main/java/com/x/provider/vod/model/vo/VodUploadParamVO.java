package com.x.provider.vod.model.vo;

import lombok.Data;

@Data
public class VodUploadParamVO {
    private String signature;
    private String videoPath;
    private String coverPath;
}
