package com.x.provider.pay.enums;

public interface BaseEnum {

    /**
     * 通过反射获得枚举
     * @param type 基本枚举类的实现类
     * @param code code码
     * @return code对应type内的枚举
     */
    static BaseEnum getEnum(Class<? extends BaseEnum> type, int code) {
        // 通过反射得到
        for (BaseEnum enumConstant : type.getEnumConstants()) {
            if (enumConstant.getCode() == code) {
                return enumConstant;
            }
        }
        return null;
    }

    int getCode();

}