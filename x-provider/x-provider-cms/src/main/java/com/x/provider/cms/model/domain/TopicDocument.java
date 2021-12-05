package com.x.provider.cms.model.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "topic", type = "topic",shards = 1,replicas = 0)
public class TopicDocument {

    @Field(type = FieldType.Keyword)
    private String keyword1;
    @Field(type = FieldType.Keyword)
    private String keyword2;
    @Field(type = FieldType.Keyword)
    private String keyword3;

    @Id
    private Long id;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Keyword)
    private String titleCnSpell;
    private Integer effectValue;
    private Integer sourceType;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String searchKeyWord;
    @Field(type = FieldType.Keyword)
    private String sourceId;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String topicDescription;
    private Boolean systemTopic;
}
