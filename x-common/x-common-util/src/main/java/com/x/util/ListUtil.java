package com.x.util;

import java.util.List;
import java.util.function.Consumer;

public class ListUtil {

    public static <T> void pageConsume(List<T> list, int pageSize, Consumer<List<T>> consumer){
        int startIndex = 0;
        while (startIndex < list.size()){
            int endIndex = startIndex + pageSize;
            if (endIndex >= list.size()){
                endIndex = list.size();
            }
            consumer.accept(list.subList(startIndex, endIndex));
            startIndex = endIndex;
        }
    }
}
