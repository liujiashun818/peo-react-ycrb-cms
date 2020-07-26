package cn.people.one.modules.base.config;

/**
 * Created by lml on 2017/5/10.
 */

import cn.people.one.core.aop.annotation.ExCacheEvict;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Aspect
@Component
public class RedisAspect {
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${theone.project.code}")
    private String code;

    @SuppressWarnings("unchecked")
    @Around("@annotation(evict)")
    public Object cacheEvict(final ProceedingJoinPoint pjp, ExCacheEvict evict) throws Throwable {
        Object value = pjp.proceed();
        Object target = pjp.getTarget();
        String key;
        if (StringUtils.isNotBlank(evict.key())) {
            String evictKeys[] = evict.key().split("_");
            for (int i = 0; i < evictKeys.length; i++) {
                if (StringUtils.isBlank(evictKeys[i])) {
                    continue;
                }
                Set<String> keys = redisTemplate.keys(code + ":" + evictKeys[i] + "*");
                redisTemplate.delete(keys);
            }
        } else {
            key = code + ":" + target.getClass().getSimpleName().toLowerCase() + "*";
            Set<String> keys = redisTemplate.keys(key);
            redisTemplate.delete(keys);
        }
        return value;
    }
}
