package com.x.provider.api.vod.model.ao;

import com.x.provider.api.vod.enums.MediaTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
