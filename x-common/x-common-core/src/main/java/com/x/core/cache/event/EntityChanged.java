package com.x.core.cache.event;

import com.x.core.enums.EntityChangeEnum;

public class EntityChanged<T> {
    private T entity;
    private EntityChangeEnum changeType;

    public EntityChanged(T entity){
        this.entity = entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    public void setChangeType(EntityChangeEnum changeType) {
        this.changeType = changeType;
    }

    public EntityChangeEnum getChangeType() {
        return changeType;
    }
}
