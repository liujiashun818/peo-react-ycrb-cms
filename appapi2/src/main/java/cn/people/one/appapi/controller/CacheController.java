package cn.people.one.appapi.controller;

import cn.people.one.appapi.cache.CacheRepository;
import cn.people.one.appapi.util.CacheKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 缓存相关接口
 * <p>
 * Created by wilson on 2018-11-20.
 */
@Slf4j
@ApiIgnore
@RestController
@RequestMapping("/api/v2/cache")
public class CacheController {

    @Autowired
    private CacheRepository cacheRepository;

    @DeleteMapping("/delete/article/{id}")
    public String deleteArticle(@PathVariable("id") Long id) {
        String key = CacheKeyUtils.getArticleDetailKey(id);
        cacheRepository.delete(key);
        return "success";
    }

    @DeleteMapping("/delete/live/{id}")
    public String deleteLive(@PathVariable("id") Long id) {
        String key = CacheKeyUtils.getLiveRoomKey(id);
        cacheRepository.delete(key);
        return "success";
    }

}
