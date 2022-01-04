package com.x.provider.general.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentStatistic {
    private Long id;
    private long starCount;
    private long replyCount;
}
