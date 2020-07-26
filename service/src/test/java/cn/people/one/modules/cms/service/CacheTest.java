package cn.people.one.modules.cms.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by lml on 2016/12/27.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheTest {

    @Autowired
    private CacheManager caffeineCacheManager;

    @Test
    public void getCacheStatus(){
        Cache cache = caffeineCacheManager.getCache("CACHE_CATEGORY_LIST");
        assert (cache !=null):"getDetailTest  Error";
    }
}
