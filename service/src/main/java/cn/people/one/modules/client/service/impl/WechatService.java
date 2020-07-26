package cn.people.one.modules.client.service.impl;

import cn.people.one.modules.client.model.front.WechatVO;
import cn.people.one.modules.client.service.IWechatService;
import cn.people.one.modules.client.utils.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by sunday on 2018/10/24.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class WechatService implements IWechatService {

    @Value("${wechat.appId}")
    private String appId;

    @Value("${wechat.appSecret}")
    private String appSecret;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 微信二次分享时获取签名认证接口
     * @return
     */
    public WechatVO getWechatInfo(String url){
        WechatVO wechatVO = new WechatVO();
        String accessToken = WechatUtil.getAccessToken(appId,appSecret,redisTemplate);
        String ticket = WechatUtil.getTicket(accessToken);
        if("40001".equals(ticket)) {
            // accessToken 失效 则重新获取
            WechatUtil.clearRedisWxAccesStoken(appId,redisTemplate);

            accessToken = WechatUtil.getAccessToken(appId,appSecret,redisTemplate);
            ticket = WechatUtil.getTicket(accessToken);
        }

        wechatVO.setAccessToken(accessToken);
        wechatVO.setAppId(appId);
        String noncestr = UUID.randomUUID().toString();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        wechatVO.setNonceStr(noncestr);
        wechatVO.setTimestamp(timestamp);
        wechatVO.setUrl(url);
        String pararms = "jsapi_ticket="+ticket+"&noncestr="+noncestr
                +"&timestamp="+timestamp+"&url="+url;
        String signature = WechatUtil.SHA1(pararms);
        wechatVO.setSignature(signature);
        return wechatVO;
    }
}
