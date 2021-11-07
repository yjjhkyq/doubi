package com.x.provider.api.vod.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaInfoDTO {
    private long id;
    private String fileId;
    private String coverUrl;
    private String type;
    private String mediaUrl;
    private long size;
    private long height;
    private long width;
    private double duration;
    private String name;
    private String description;
}
