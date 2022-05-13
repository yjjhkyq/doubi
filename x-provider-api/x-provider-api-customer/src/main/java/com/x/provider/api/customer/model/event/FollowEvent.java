package com.x.provider.api.customer.model.event;

import com.x.core.enums.IntegerEnum;
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

  private Integer eventType;

  public enum EventTypeEnum implements IntegerEnum {
    FOLLOW(1),
    UN_FOLLOW(2),
    ;

    private Integer value;

    EventTypeEnum(Integer value){
      this.value = value;
    }

    @Override
    public Integer getValue() {
      return value;
    }
  }
}

