package com.x.provider.video.model.ao;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CreateVideoAO {
    @NotBlank
    private String title;
    @NotBlank
    private String fileId;
    private List<Long> atUsers;
}
