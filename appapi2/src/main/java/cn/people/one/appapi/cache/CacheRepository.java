package cn.people.one.appapi.cache;

import cn.people.one.appapi.constant.CacheConstant;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wilson on 2018-11-05.
 */
@Repository
public class CacheRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Cache string value
     *
     * @param key   cache key
     * @param value cache value
     * @param time  time of seconds
     */
    private void cache(String key, String value, Long time) {
        redisTemplate.opsForValue().set(key,
                value == null ? CacheConstant.Value.NULL : value,
                value == null ? CacheConstant.Time.ONE_MINUTE : time,
                TimeUnit.SECONDS);
    }

    /**
     * Cache Object value
     *
     * @param key   cache key
     * @param value cache value
     * @param time  time of seconds
     */
    public void cache(String key, Object value, Long time) {
        cache(key, JSON.toJSONString(value), time);
    }

    /**
     * Cache Object value
     *
     * @param key   cache key
     * @param value cache value
     * @param time  time of seconds
     */
    public void cache(String key, List<?> value, Long time) {
        cache(key, JSON.toJSONString(value), time);
    }

    /**
     * Get cache
     *
     * @param key cache key
     */
    private Cache<String> getString(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return new Cache<>(CacheStatus.NOT_CACHING);
        }

        if (CacheConstant.Value.NULL.equals(value)) {
            return new Cache<>(CacheStatus.CACHING_NULL);
        }

        return new Cache<>(value);
    }

    /**
     * Get object cache
     *
     * @param key
     * @param clazz
     */
    public <T> Cache<T> getObject(String key, Class<T> clazz) {
        Cache<String> cache = getString(key);
        if (cache.getStatus() == CacheStatus.CACHING) {
            return new Cache<>(JSON.parseObject(cache.getObject(), clazz));
        } else {
            return new Cache<>(cache.getStatus());
        }
    }

    /**
     * Get list cache
     *
     * @param key
     * @param clazz
     */
    public <T> Cache<T> getList(String key, Class<T> clazz) {
        Cache<String> cache = getString(key);
        if (cache.getStatus() == CacheStatus.CACHING) {
            return new Cache<>(JSON.parseArray(cache.getObject(), clazz));
        } else {
            return new Cache<>(cache.getStatus());
        }
    }

    /**
     * Del cache
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Del cache
     *
     * @param keys
     */
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

}
