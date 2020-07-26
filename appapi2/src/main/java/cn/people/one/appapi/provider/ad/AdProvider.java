package cn.people.one.appapi.provider.ad;

import cn.people.one.appapi.cache.Cache;
import cn.people.one.appapi.cache.CacheRepository;
import cn.people.one.appapi.cache.CacheStatus;
import cn.people.one.appapi.constant.CacheConstant;
import cn.people.one.appapi.provider.ad.converter.AdConverter;
import cn.people.one.appapi.provider.ad.model.Ad;
import cn.people.one.appapi.provider.ad.model.AdResult;
import cn.people.one.appapi.provider.ad.model.WebView;
import cn.people.one.appapi.util.CacheKeyUtils;
import cn.people.one.appapi.util.NumberUtils;
import cn.people.one.appapi.vo.ArticleDetailVO;
import cn.people.one.appapi.vo.ArticleVO;
import com.alibaba.fastjson.JSON;
import com.github.kevinsawicki.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by wilson on 2018-10-26.
 */
@Slf4j
@Component
public class AdProvider {

    @Value("${theone.ad.url}")
    private String adUrl;

    @Autowired
    private CacheRepository cacheRepository;

    /**
     * 从缓存中获取广告数据
     * @return
     */
    public List<ArticleVO> getListAds(Long categoryId) {
        List<Ad> commons = listAdsCache(3, null);
        List<Ad> customs = listAdsCache(3, categoryId);

        TreeMap<Integer, Ad> ads = new TreeMap<>();
        if (commons != null) {
            for (Ad ad : commons) {
                ads.put(ad.getMessageOrder(), ad);
            }
        }

        if (customs != null) {
            for (Ad ad : customs) {
                ads.put(ad.getMessageOrder(), ad);
            }
        }

        return AdConverter.toArticleVO(ads.values());
    }

    /**
     * 从缓存中获取广告数据
     * @return
     */
    public List<WebView> getDetailAds() {

        String key = CacheKeyUtils.getAdsDetailKey(5);
        Cache<String> cache = cacheRepository.getObject(key, String.class);
        List<Ad> commons = null;
        if (cache.getStatus() == CacheStatus.CACHING) {
            commons = JSON.parseArray(cache.getObject(),Ad.class);
        }
        //  List<Ad> commons = listAds(5, null);
        return AdConverter.toWebView(commons);
    }

    /**
     * 详情广告 缓存 异步
     */
    @Async
    public void cacheDetailAds() {
        log.info("详情广告 缓存 异步处理");
        try {
            String key = CacheKeyUtils.getAdsDetailKey(5);
            List<Ad> commons = listAds(5, null);
            String json = JSON.toJSONString(commons);
            cacheRepository.cache(key,json,1200L);
        } catch (Exception e) {
            log.error("详情广告 缓存 异常",e);
        }
    }

    private List<Ad> listAdsCache(Integer type, Long categoryId) {

        String key = CacheKeyUtils.getAdsListKey(type,categoryId);
        Cache<String> cache = cacheRepository.getObject(key, String.class);
        List<Ad> commons = null;
        if (cache.getStatus() == CacheStatus.CACHING) {
            commons = JSON.parseArray(cache.getObject(),Ad.class);
        }
        return commons;
    }

    /**
     * 列表广告 缓存 异步
     */
    @Async
    public void cacheListAds(Long categoryId) {
        log.info("列表广告common 缓存 异步处理");
        try {
            String key = CacheKeyUtils.getAdsListKey(3,null);
            List<Ad> commons = listAds(3, null);
            String json = JSON.toJSONString(commons);
            cacheRepository.cache(key,json,1200L);
        } catch (Exception e) {
            log.error("列表广告common 缓存 异常",e);
        }

        log.info("列表广告customs 缓存 异步处理");
        try {
            String key = CacheKeyUtils.getAdsListKey(3,categoryId);
            List<Ad> customs = listAds(3, categoryId);
            String json = JSON.toJSONString(customs);
            cacheRepository.cache(key,json,1200L);
        } catch (Exception e) {
            log.error("列表广告customs 缓存 异常",e);
        }
    }

    private List<Ad> listAds(Integer type, Long categoryId) {
        if (type == null) {
            return Collections.emptyList();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("view_type", type);
        if (categoryId != null) params.put("channel", categoryId);

        HttpRequest request = HttpRequest.get(adUrl + "/News", params, true);
        if (!request.ok()) {
            log.error("Request ad error {} -> {}", request.url().toString(), request.body());
            return Collections.emptyList();
        }

        try {
            String body = request.body();
            log.info("{} -> {}", request.url().toString(), body);
            AdResult result = JSON.parseObject(body, AdResult.class);
            if (NumberUtils.equals(200, result.getErrorCode())) {
                return result.getData();
            } else {
                log.error("Request ad error -> {}", body);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return Collections.emptyList();
    }

}
