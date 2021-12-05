package com.x.provider.cms.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "video", type = "video",shards = 1,replicas = 0)
public class VideoDocument {
    @Id
    private Long id;
    private Long customerId;
    private String fileId;
    private String coverUrl;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;
    private double duration;
    private Date updatedOnUtc;
    private Date createdOnUtc;
}
