package com.paascloud.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * spring redis 工具类
 * 
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
public class RedisService {
    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofDays(2));
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObjectIfAbsent(final String key, final T value) {
        redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofDays(2));
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final Duration timeout) {
        return expire(key, timeout.getSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    public <T> List<T> listCacheObject(final List<String> keys){
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.multiGet(keys);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> Optional<T> getOptionalCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return Optional.ofNullable(operation.get(key));
    }

    public Long getLongCacheObject(final String key) {
        ValueOperations<String, Integer> operation = redisTemplate.opsForValue();
        Integer value = operation.get(key);
        return value != null ? value.longValue() : null;
    }
    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key, Supplier<T> supplier) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        T result = operation.get(key);
        if (result == null) {
            result = supplier.get();
            if (result != null) {
                setCacheObjectIfAbsent(key, result);
            }
        }
        return result;
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> long setCacheSet(final String key, final Set<T> dataSet) {
        Long count = redisTemplate.opsForSet().add(key, dataSet);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Optional<Map<String, T>>getCacheMapOptional(final String key) {
        return Optional.ofNullable(redisTemplate.opsForHash().entries(key));
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 删除hash值
     *
     * @param key
     * @param hKey
     */
    public void deleteCacheMapValue(final String key, final String hKey) {
        redisTemplate.opsForHash().delete(key, hKey);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> Optional<T> getCacheMapValueOptional(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return Optional.ofNullable(opsForHash.get(key, hKey));
    }


    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 是否存在key
     *
     * @param key
     * @return true 存在key 反之false
     */
    public Boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }

    public <T> Boolean zadd(final String key, T value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    public <T> Long zremove(final String key, T value) {
        return redisTemplate.opsForZSet().remove(key, value);
    }

    public Long zsize(final String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    public <T> Set<T> reverseRange(final String key, long page, long limit) {
        return redisTemplate.opsForZSet().reverseRange(key, (page - 1) * limit, (page - 1) * limit + limit);
    }

    public Set<Long> reverseRangeLong(final String key, long page, long limit) {
        Set<Integer> values =  redisTemplate.opsForZSet().reverseRange(key, (page - 1) * limit, (page - 1) * limit + limit);
        Set<Long> result = new LinkedHashSet<>(values.size());
        values.stream().forEach(item -> {
            result.add(item.longValue());
        });
        return result;
    }

    public <T> Long zrank(final String key, T value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * redis 锁
     * @param key
     * @param value
     * @return
     */
    public boolean tryLock(String key, String value) {
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        if (operation.setIfAbsent(key, value, Duration.ofMinutes(3))) {
            return true;
        }
        String currentValue = getCacheObject(key);
        if (value.equals(currentValue)) {
            //获取上一个锁的时间 如果高并发的情况可能会出现已经被修改的问题  所以多一次判断保证线程的安全
            String oldValue = operation.getAndSet(key, value);
            if (currentValue.equals(oldValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Redis解锁的操作
     *
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        String currentValue = operation.get(key);
        if (value.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }
}
