package com.x.provider.video.model.ao;

import lombok.Data;

import java.util.List;

@Data
public class CreateVideoAO {
    private long id;
    private String title;
    private String fileId;
    private List<Long> atUsers;
}
