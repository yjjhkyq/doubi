package com.x.provider.api.oss.model.dto.vod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListMediaRequestDTO {
    private List<String> fileIdList;
}
