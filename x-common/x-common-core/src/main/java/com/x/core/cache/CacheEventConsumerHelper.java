package com.x.core.cache;

import com.x.core.cache.event.EntityChanged;

import java.util.function.Consumer;

public class CacheEventConsumerHelper {

    public static  <T> void onEntityChangedEvent(EntityChanged<T> entityChanged, Consumer<T> inserted, Consumer<T> updated, Consumer<T> deleted){
        switch (entityChanged.getChangeType()){
            case INSERTED:
                if (inserted != null){
                    inserted.accept(entityChanged.getEntity());
                }
                break;
            case UPDATED:
                if (updated != null){
                    updated.accept(entityChanged.getEntity());
                }
                break;
            case DELETED:
                if (deleted != null){
                    deleted.accept(entityChanged.getEntity());
                }
                break;
            default:
                break;
        }
    }
}
