package com.x.core.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class BeanUtil {

    public static <T,R> List<R> prepare(List<T> source, Class<R> destCls){
        List<R> result = new ArrayList<>(source.size());
        try {
            for (T t: source) {
                R r = destCls.getConstructor().newInstance();
                BeanUtils.copyProperties(t, r);
                result.add(r);
            }
        }
        catch (Exception e){
            throw new IllegalStateException("prepare model error: " + destCls.getSimpleName(), e);
        }
        return result;
    }

    public static <T, R> R prepare(T source, Class<R> destCls){
        try {
            if (source == null){
                return null;
            }
            R r = destCls.getConstructor().newInstance();
            BeanUtils.copyProperties(source, r);
            return r;
        }
        catch (Exception e){
            throw new IllegalStateException("prepare model error: " + destCls.getSimpleName(), e);
        }
    }
}
