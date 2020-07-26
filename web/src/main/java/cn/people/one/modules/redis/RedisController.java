package cn.people.one.modules.redis;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.base.config.RedisConfig;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lml on 2017/4/25.
 */
@Api(description = "缓存管理(redis模块)")
@RestController
@RequestMapping("/api/redis/clear")
@Slf4j
public class RedisController {

    @RequestMapping(method = RequestMethod.GET)
    @Caching(evict = {@CacheEvict(value = {RedisConfig.OFFICE_TREE}),
        @CacheEvict(value = {RedisConfig.CLIENT_TREE}),
        @CacheEvict(value = {RedisConfig.MENU_TREE}),
        @CacheEvict(value = {RedisConfig.CATEGORY_TREE})})
    public Result clear() {
        return Result.success();
    }
}
