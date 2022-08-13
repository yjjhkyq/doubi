package com.x.provider.api.mc.model.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: liushenyi
 * @date: 2022/08/12/14:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonMessageBodyDTO {
    private String content;
    private String contentUrl;
}
