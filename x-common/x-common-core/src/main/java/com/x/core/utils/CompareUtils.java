package com.x.core.utils;

import cn.hutool.core.comparator.CompareUtil;

public class CompareUtils {

    public static boolean gtZero(Long value){
        return CompareUtil.compare(value, 0L) > 0;
    }

    public static boolean gtZero(Integer value){
        return CompareUtil.compare(value, 0) > 0;
    }

}
