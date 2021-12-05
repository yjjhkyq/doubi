package com.x.provider.cms.model.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "security", type = "security",shards = 1,replicas = 0)
public class SecurityDocument {
    @Id
    private Long id;
    /**
     * 股票编码
     */
    @Field(type = FieldType.Keyword)
    private String code;
    /**
     * 股票代码
     */
    @Field(type = FieldType.Keyword)
    private String symbol;
    /**
     * 股票中文简称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;
    /**
     * 股票全称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String fullName;
    /**
     * 股票英文名
     */
    private String enName;
    /**
     * 股票拼音
     */
    @Field(type = FieldType.Keyword)
    private String cnSpell;
    /**
     * 交易所代码 SSE 上交所 SZSE深交所
     */
    @Field(type = FieldType.Keyword)
    private String exchange;
}
