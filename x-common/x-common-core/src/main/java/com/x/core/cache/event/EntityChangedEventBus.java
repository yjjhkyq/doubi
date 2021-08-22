package com.x.core.cache.event;

import com.google.common.eventbus.EventBus;
import com.x.core.enums.EntityChangeEnum;

public class EntityChangedEventBus extends EventBus {

    public <T> void postEntityInserted(EntityChanged<T> entityChanged){
        entityChanged.setChangeType(EntityChangeEnum.INSERTED);
        this.post(entityChanged);
    }

    public <T> void postEntityUpdated(EntityChanged<T> entityChanged){
        entityChanged.setChangeType(EntityChangeEnum.UPDATED);
        this.post(entityChanged);
    }

    public <T> void postEntityDeleted(EntityChanged<T> entityChanged){
        entityChanged.setChangeType(EntityChangeEnum.DELETED);
        this.post(entityChanged);
    }
}
