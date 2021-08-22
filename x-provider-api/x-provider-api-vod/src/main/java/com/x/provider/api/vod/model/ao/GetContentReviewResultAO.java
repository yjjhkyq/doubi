package com.x.provider.api.vod.model.ao;


import lombok.Data;

import java.util.List;

@Data
public class GetContentReviewResultAO {
    private String notifyUrl;
    private List<String> fileIds;
}
