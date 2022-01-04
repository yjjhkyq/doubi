package com.x.provider.video.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoStatistic {
    private long id;
    private long starCount;
    private long commentCount;
    private long playCount;
}
