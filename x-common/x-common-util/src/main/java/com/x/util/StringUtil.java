package com.x.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {
    public static String DEFAULT_SPLITTER = ",";

    public static List<Long> parse(String source){
        return parse(source, DEFAULT_SPLITTER);
    }

    public static String toString(Collection<Long> source){
        return toString(source, DEFAULT_SPLITTER);
    }

    public static List<Long> parse(String source, String splitter){
        if (source == null){
            return new ArrayList<>();
        }
        String[] split = source.split(splitter);
        if (split.length == 0){
            return new ArrayList<>();
        }
        List<Long> result = new ArrayList<>(split.length);
        Arrays.stream(split).forEach(item -> {
            result.add(Long.parseLong(item.trim()));
        });
        return result;
    }

    public static String toString(Collection<Long> source, String splitter){
        if (source.isEmpty()){
            return "";
        }
        return String.join(splitter, source.stream().map(String::valueOf).collect(Collectors.toList()));
    }
}
