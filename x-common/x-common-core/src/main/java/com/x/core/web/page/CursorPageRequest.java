package com.x.core.web.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageRequest {

    public static final String START_CURSOR_DEFAULT = "0";

    /** 每页显示记录数 */
    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    @ApiModelProperty(value = "分页游标, 第一页请求cursor是0, response中会返回下一页请求用到的cursor, 同时response还会返回has_more来表明是否有更多的数据。")
    private String cursor;

    public Long getDescOrderCursor(){
        if (START_CURSOR_DEFAULT.equals(cursor)){
            return Long.MAX_VALUE;
        }
        return Long.parseLong(cursor);
    }

    public Long getLongCursor(){
        return Long.parseLong(cursor);
    }
}
