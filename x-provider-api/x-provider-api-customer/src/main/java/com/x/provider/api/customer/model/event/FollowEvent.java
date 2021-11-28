package com.x.provider.api.customer.model.event;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FollowEvent {
  private Long id;
  private Long fromCustomerId;
  private Long toCustomerId;
  private Integer relation;
  private boolean firstFollow;
}
