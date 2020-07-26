package cn.people.one.modules.client.utils;

import cn.people.one.core.util.http.HttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

/**
 * 微信相关工具类
 * Created by sunday on 2018/10/24.
 */
@Slf4j
public class WechatUtil {

    // redis accessToken key : APPID
    private static final String REDIS_KEY_WX_ACCES_STOKEN= "WX:ACCES_STOKEN:";

    /**
     * 获取accessToken
     * @param appId
     * @param appSecret
     * @return
     */
    public static String getAccessToken(String appId,String appSecret, RedisTemplate<String, String> redisTemplate){
        String rediskey = getRedisKeyWxAccesStoken(appId);
        String accessToken = redisTemplate.opsForValue().get(rediskey);

        if (accessToken == null || "null" .equals(accessToken)) {
            String pararms = "grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
            String info = HttpUtils.sendGet("https://api.weixin.qq.com/cgi-bin/token", pararms, null);

            log.info("WechatUtil.getAccessToken:{}", info);
            JSONObject result = JSON.parseObject(info);
            accessToken = String.valueOf(result.get("access_token"));

            if (accessToken != null) {
                // 设置7100 < 微信定制7200 秒
                redisTemplate.opsForValue().set(rediskey,accessToken,7100, TimeUnit.SECONDS);
            }
        }
        return accessToken;
    }

    /**
     * redis accessToken key : APPID
     * @param appId
     * @return
     */
    public static String getRedisKeyWxAccesStoken(String appId) {
        return REDIS_KEY_WX_ACCES_STOKEN+appId;
    }

    /**
     * 清除WxAccesStoken 缓存
     * @param appId
     * @return
     */
    public static void clearRedisWxAccesStoken(String appId, RedisTemplate<String, String> redisTemplate) {
        redisTemplate.delete(getRedisKeyWxAccesStoken(appId));
    }

    /**
     * 获取票据
     * @param accessToken
     * @return
     */
    public static String getTicket(String accessToken){
        String pararms = "type=jsapi&access_token=" + accessToken;
        String info = HttpUtils.sendGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket", pararms, null);
        log.info("调用微信二次分享接口响应结果 getTicket：" + info);
        JSONObject result = JSON.parseObject(info);
        String ticket = String.valueOf(result.get("ticket"));
        return ticket;
    }

    public static String SHA1(String str){
        if(str == null || str.length()==0){
            return null;
        }
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j*2];
            int k = 0;
            for(int i=0;i<j;i++){
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

}
