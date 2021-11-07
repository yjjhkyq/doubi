package com.x.redis.domain;

import org.springframework.data.redis.core.ZSetOperations;

public class LongTypeTuple implements ZSetOperations.TypedTuple<Long> {
    private Long value;
    private Double score;
    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public Double getScore() {
        return score;
    }

    @Override
    public int compareTo(ZSetOperations.TypedTuple<Long> o) {
        LongTypeTuple orgin = (LongTypeTuple)o;
        return this.getScore().compareTo(o.getScore());
    }
}
