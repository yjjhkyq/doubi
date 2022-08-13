package com.x.provider.api.oss.model.dto.oss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GreenRequestDTO {
    /**
     * 需要审核的数据，文本直接填入文本内容，图片视频填入图片访问url
     */
    private String value;
    /**
     * 数据的数据类型，PICTURE 图片、VIDEO视频、TEXT文本
     */
    private String dataType;
}
