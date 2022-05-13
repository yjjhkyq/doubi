package com.x.provider.api.customer.enums;

public enum CustomerRelationEnum {
    NO_RELATION(0),
    FOLLOW(1),
    FRIEND(2),
    FANS(3)
    ;

    private int value;


    CustomerRelationEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CustomerRelationEnum valueOf(int value) {
        for (CustomerRelationEnum type : CustomerRelationEnum.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalStateException("value:" + value);
    }
}
