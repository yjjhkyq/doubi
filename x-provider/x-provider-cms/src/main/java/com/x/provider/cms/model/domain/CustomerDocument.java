package com.x.provider.cms.model.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "customer", type = "customer",shards = 1,replicas = 0)
public class CustomerDocument {
    @Id
    private Long id;
    @Field(type = FieldType.Keyword)
    private String userName;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String nickName;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String signature;
    private String avatarId;
    private String avatarUrl;
    private Date createdOnUtc;
}
