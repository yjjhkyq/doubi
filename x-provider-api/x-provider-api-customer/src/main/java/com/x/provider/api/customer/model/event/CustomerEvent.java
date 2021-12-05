package com.x.provider.api.customer.model.event;

import com.x.core.enums.IntegerEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEvent {
    private Integer eventType;

    private Long id;
    private String userName;
    private String email;
    private Boolean active;
    private Boolean systemAccount;
    private Date createdOnUtc;
    private Date updatedOnUtc;
    private String phone;
    private boolean registerRole;

    private CustomerAttributeEvent customerAttributeEvent;

    public enum EventTypeEnum implements IntegerEnum {
        ADD(1),
        UPDATE(2),
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
