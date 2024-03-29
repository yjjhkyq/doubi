package com.x.provider.api.oss.model.dto.vod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteMediaRequestDTO {
    private String fileId;
}
