package com.x.provider.api.customer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericAttributeDTO {
  private Long id;
  private Long entityId;
  private String keyGroup;
  private String key;
  private String value;
  private Integer suggestionType;
  private String valueUrl;
}
