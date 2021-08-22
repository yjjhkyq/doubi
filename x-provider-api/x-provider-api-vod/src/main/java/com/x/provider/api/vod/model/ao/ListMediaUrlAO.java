package com.x.provider.api.vod.model.ao;

import com.x.provider.api.vod.enums.MediaTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class ListMediaUrlAO {
    /**
     * 文件id
     */
    private List<String> fileIds;
    /**
     * 媒体类型
     */
    private MediaTypeEnum mediaType;
}
