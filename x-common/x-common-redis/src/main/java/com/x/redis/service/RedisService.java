package com.x.redis.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.x.redis.domain.LongTypeTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * spring redis 工具类
 * 
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
public class RedisService {

    @Autowired
    public StringRedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, toJsonStr(value), Duration.ofDays(2));
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObjectIfAbsent(final String key, final T value) {
        redisTemplate.opsForValue().setIfAbsent(key, toJsonStr(value), Duration.ofDays(2));
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
        redisTemplate.opsForValue().set(key, toJsonStr(value), timeout, timeUnit);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, Duration timeout) {
        redisTemplate.opsForValue().set(key, toJsonStr(value), timeout);
    }

    /**
     * 缓存计数器, 每次调用会刷新过期时间
     * @param key 键
     * @param timeout 过期时间
     */
    public void setCountObject(final String key, Duration timeout) {
        redisTemplate.opsForValue().increment(key);
        expire(key, timeout);
    }

    /**
     * 获得key计数器里的值, 不刷新过期时间
     * @param key 键
     * @return 值大小
     */
    public Integer getCountObject(final String key) {
        String res = redisTemplate.opsForValue().get(key);
        return StringUtils.isEmpty(res) ? 0 : toBean(res, Integer.class);
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
    public <T> T getCacheObject(final String key, Class<T> cls) {
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        String jsonObject = operation.get(key);
        if (jsonObject == null){
            return null;
        }
        return toBean(jsonObject, cls);
    }

    public <T> List<T> listCacheObject(final List<String> keys, Class<T> cls){
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        List<String> strResult = operation.multiGet(keys).stream().filter(item -> item != null).collect(Collectors.toList());
        List<T> result = new ArrayList<>(strResult.size());
        strResult.forEach(item -> {
            result.add(toBean(item, cls));
        });
        return result;
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> Optional<T> getOptionalCacheObject(final String key, Class<T> cls) {
        return Optional.ofNullable(getCacheObject(key, cls));
    }

    public Long getLongCacheObject(final String key) {
        return getCacheObject(key, Long.class);
    }
    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key, Supplier<T> supplier, Class<T> cls) {
        T result = getCacheObject(key, cls);
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
    public <T> long setCacheList(final String key, final List<T> dataList, Class<T> cls) {
        List<String> dataStrList = new ArrayList<>(dataList.size());
        dataList.stream().forEach(item -> {
            dataStrList.add(toJsonStr(item));
        });
        Long count = redisTemplate.opsForList().rightPushAll(key, dataStrList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key, Class<T> cls) {
        List<String>  strResult = redisTemplate.opsForList().range(key, 0, -1);
        if (CollectionUtil.isEmpty(strResult)){
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(strResult.size());
        strResult.forEach(item -> {
            result.add(toBean(item, cls));
        });
        return result;
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> long setCacheSet(final String key, final Set<T> dataSet) {
        String[] strSet = new String[dataSet.size()];
        int index = 0;
        for (T item : dataSet){
            strSet[index++] = toJsonStr(item);
        }
        Long count = redisTemplate.opsForSet().add(key, strSet);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key, Class<T> cls) {
        Set<String> strSet = redisTemplate.opsForSet().members(key);
        if (CollectionUtil.isEmpty(strSet)){
            return new LinkedHashSet<>();
        }
        return strSet.stream().map(item ->toBean(item, cls)).collect(Collectors.toSet());
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            Map<String, String> strDataMap= new HashMap<>(dataMap.size());
            dataMap.entrySet().forEach(item -> {
                strDataMap.put(item.getKey(), toJsonStr(item.getValue()));
            });
            redisTemplate.opsForHash().putAll(key, strDataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key, Class<T> cls) {
        Map<Object, Object> strMap = redisTemplate.opsForHash().entries(key);
        if (CollectionUtil.isEmpty(strMap)){
            return new HashMap<>();
        }
        return strMap.entrySet().stream().collect(Collectors.toMap(item -> String.valueOf(item.getKey()), item -> toBean(String.valueOf(item.getValue()), cls)));
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Optional<Map<String, T>>getCacheMapOptional(final String key,  Class<T> cls) {
        return Optional.ofNullable(getCacheMap(key, cls));
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, toJsonStr(value));
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
    public <T> T getCacheMapValue(final String key, final String hKey, Class<T> cls) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        return toBean(opsForHash.get(key, hKey), cls);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> Optional<T> getCacheMapValueOptional(final String key, final String hKey,  Class<T> cls) {
        return Optional.ofNullable(getCacheMapValue(key, hKey, cls));
    }


    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys, Class<T> cls) {
        List<Object> strResult = redisTemplate.opsForHash().multiGet(key, hKeys);
        if (CollectionUtil.isEmpty(strResult)){
            return new ArrayList<>();
        }
        return strResult.stream().map(item -> toBean(String.valueOf(item), cls)).collect(Collectors.toList());
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
        return redisTemplate.opsForZSet().add(key, toJsonStr(value), score);
    }

    public Long zadd(final String key, Set<LongTypeTuple> values) {
        Set<ZSetOperations.TypedTuple<String>> strSet = new LinkedHashSet<>(values.size());
        values.forEach(item -> {
            strSet.add(new DefaultTypedTuple<String>(item.getValue().toString(), item.getScore()));
        });
        return redisTemplate.opsForZSet().add(key, strSet);
    }

    public <T> Long zremove(final String key, T value) {
        return redisTemplate.opsForZSet().remove(key, value);
    }

    public Long zsize(final String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    public <T> Set<T> reverseRange(final String key, long page, long limit, Class<T> cls) {
        Set<String> strSet = redisTemplate.opsForZSet().reverseRange(key, (page - 1) * limit, (page - 1) * limit + limit);
        if (CollectionUtil.isEmpty(strSet)){
            return new LinkedHashSet<>();
        }
        return strSet.stream().map(item -> toBean(item, cls)).collect(Collectors.toSet());
    }

    public <T> Set<T> reverseRangeByCursor(final String key, long cursor, long limit, Class<T> cls) {
        Set<String> strSet = redisTemplate.opsForZSet().reverseRange(key, cursor, cursor + limit);
        if(CollectionUtil.isEmpty(strSet)){
            return new LinkedHashSet<>();
        }
        return strSet.stream().map(item -> toBean(item, cls)).collect(Collectors.toSet());
    }

    public <T> Set<T> rangeByScore(final String key, long startScore, long count, Class<T> cls){
        Set<String> strSet = redisTemplate.opsForZSet().rangeByScore(key, startScore, Long.MAX_VALUE, 0, count);
        if (CollectionUtil.isEmpty(strSet)){
            return new LinkedHashSet<>();
        }
        return strSet.stream().map(item -> toBean(item, cls)).collect(Collectors.toSet());
    }

    public <T> Set<ZSetOperations.TypedTuple<T>> reverseRangeByScore(final String key, double startScore, double endScore, long start, long count, Class<T> cls){
        Set<ZSetOperations.TypedTuple<String>> strSet = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, startScore, endScore, start, count);
        if (CollectionUtil.isEmpty(strSet)){
            return new LinkedHashSet<>();
        }
        return strSet.stream().map(item -> new DefaultTypedTuple<T>(toBean(item.getValue(), cls), item.getScore())).collect(Collectors.toSet());
    }

    public <T> Set<ZSetOperations.TypedTuple<T>> reverseRangeByScore(final String key, double cursor, long count, Class<T> cls){
        if (cursor == 0){
            cursor = Long.MAX_VALUE;
        }
        Set<ZSetOperations.TypedTuple<String>> strSet = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, cursor, 0, count);
        if (CollectionUtil.isEmpty(strSet)){
            return new LinkedHashSet<>();
        }
        return strSet.stream().map(item -> new DefaultTypedTuple<T>(toBean(item.getValue(), cls), item.getScore())).collect(Collectors.toSet());
    }

    public  Set<LongTypeTuple> reverseRangeByScoreLong(final String key, double cursor, long count){
        if (cursor == 0){
            cursor = Long.MAX_VALUE;
        }
        Set<ZSetOperations.TypedTuple<Long>> values = reverseRangeByScore(key, cursor, count, Long.class);
        Set<LongTypeTuple> result = new LinkedHashSet<>(values.size());
        values.forEach(item -> {
            result.add(new LongTypeTuple(item.getValue(), item.getScore()));
        });
        return result;
    }

    public Set<Long> rangeByScoreLong(final String key, long startScore, long count){
        Set<Integer> values = rangeByScore(key, startScore, count, Integer.class);
        Set<Long> result = new LinkedHashSet<>(values.size());
        values.stream().forEach(item -> {
            result.add(item.longValue());
        });
        return result;
    }

    public Set<Long> reverseRangeLong(final String key, long page, long limit) {
        Set<String> values =  redisTemplate.opsForZSet().reverseRange(key, (page - 1) * limit, (page - 1) * limit + limit);
        if (CollectionUtil.isEmpty(values)){
            return new LinkedHashSet<>();
        }
        Set<Long> result = new LinkedHashSet<>(values.size());
        values.stream().forEach(item -> {
            result.add(toBean(item, Long.class));
        });
        return result;
    }

    public <T> Set<T> range(final String key, Class<T> cls){
        Set<String> strSet = redisTemplate.opsForZSet().range(key, 0, -1);
        if (CollectionUtil.isEmpty(strSet)){
            return new LinkedHashSet<>();
        }
        return strSet.stream().map(item -> toBean(item, cls)).collect(Collectors.toSet());
    }

    public Set<Long> rangeLong(final String key){
        Set<Long> values = range(key, Long.class);
        Set<Long> result = new LinkedHashSet<>(values.size());
        values.stream().forEach(item -> {
            result.add(item.longValue());
        });
        return result;
    }

    public <T> Long zrank(final String key, T value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    public Long incr(String key, String field, long value){
        return redisTemplate.opsForHash().increment(key, field, value);
    }

    public Double incr(String key, String field, double value){
        return redisTemplate.opsForHash().increment(key, field, value);
    }



    /**
     * redis 锁
     * @param key
     * @param value
     * @return
     */
    public boolean tryLock(String key, String value) {
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        if (operation.setIfAbsent(key, value, Duration.ofMillis(200))) {
            return true;
        }
        String currentValue = getCacheObject(key, String.class);
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

    public Set<Long> dynamicPage(String key,  long timeout, TimeUnit unit, long cursor, int pageSize, Supplier<Set<ZSetOperations.TypedTuple<Long>>> initSupplier){
        if (cursor == 1){
            Set<ZSetOperations.TypedTuple<Long>> initData = initSupplier.get();
            if (!initData.isEmpty()){
                redisTemplate.opsForZSet().add(key, initData.stream().map(item -> new DefaultTypedTuple<String>(toJsonStr(item), item.getScore())).collect(Collectors.toSet()));
            }
            redisTemplate.expire(key, timeout, unit);
        }
        return reverseRangeLong(key, cursor, pageSize);
    }

    public void convertAndSend(String channel, String message){
        redisTemplate.convertAndSend(channel, message);
    }

    private <T> T toBean(String src, Class<T> cls){
        if (cls.equals(Long.class)){
            return (T) Long.valueOf(src);
        }
        if (cls.equals(Integer.class)){
            return (T) Integer.valueOf(src);
        }
        if (cls.equals(Double.class)){
            return (T) Double.valueOf(src);
        }
        if(cls.equals(String.class)){
            return (T) src;
        }
        return JSONUtil.toBean(src, cls);
    }

    private <T> String toJsonStr(T t){
        if (t instanceof Number || t instanceof String){
            return t.toString();
        }
        return JSONUtil.toJsonStr(t);
    }
}
