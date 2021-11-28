package com.x.provider.api.mc.model.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 通用消息体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralMsgBody {
    /**
     * 标题
     */
    private String title;
    /**
     * 内容，不为空时显示
     */
    private String content;
    /**
     * 用表格的形式显示此内容，键为字段名 值为对应的值
     */
    private Map<String, String> tableData;
}
