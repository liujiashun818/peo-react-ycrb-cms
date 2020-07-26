package cn.people.one.modules.client.service.impl;

import cn.people.one.core.util.http.HttpUtils;
import cn.people.one.modules.client.service.IVersionService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunday on 2018/10/23.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class VersionService implements IVersionService {

    @Value("${android.version}")
    private String androidVersionUrl;
    /**
     * 获取安卓版本信息
     * @return
     */
    @Override
    public Map getVersion(int appversion){
        log.info("获取版本信息接口地址为："+androidVersionUrl);
        String versionInfo = HttpUtils.sendGet(androidVersionUrl, null,null);
        log.info("获取版本信息结果为："+versionInfo);
        if(Lang.isEmpty(versionInfo)){
            return null;
        }
        Map resultMap=new HashMap();
        JSONObject result = JSON.parseObject(versionInfo);
        Integer version = result.getInteger("version");
        Integer force = result.getInteger("force");
        if(version == null){
            return null;
        }
        /**
         * 1升级 0不需要升级（最新版本）2:强制更新
         */
        Integer type;
        if(appversion<version){
            if(force==0){
                type = 1;
            }else{
                type = 2;
            }
        }else{
            type=0;
        }
        resultMap.put("version",version);
        resultMap.put("type",type);
        resultMap.put("description",result.get("description"));
        resultMap.put("down_url",result.get("download_url"));
        return resultMap;
    }
}
