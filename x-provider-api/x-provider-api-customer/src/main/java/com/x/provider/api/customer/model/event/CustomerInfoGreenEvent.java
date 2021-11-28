package com.x.provider.api.customer.model.event;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CustomerInfoGreenEvent {
  private Long customerId;
  private boolean pass;
}
