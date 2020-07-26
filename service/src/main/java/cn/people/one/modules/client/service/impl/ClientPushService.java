package cn.people.one.modules.client.service.impl;

import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.core.util.time.AiuiTimeUtils;
import cn.people.one.core.util.time.DateHelper;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.client.model.ClientPush;
import com.gexin.rp.sdk.base.notify.Notify;
import cn.people.one.modules.client.model.front.ClientPushVO;
import cn.people.one.modules.client.service.IClientPushService;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.service.IArticleService;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.dto.GtReq;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by lml on 17-2-27.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class ClientPushService extends BaseService<ClientPush> implements IClientPushService {

    private static JsonMapper jsonMapper = JsonMapper.defaultMapper();

    @Value("${gexin.appId}")
    private String appId;

    @Value("${gexin.appKey}")
    private String appKey;

    @Value("${gexin.masterSecret}")
    private String masterSecret;

    @Value("${gexin.url}")
    private String url;

    @Value("${theone.project.code}")
    private String code;

    @Autowired
    private IArticleService articleService;

    @Override
    public Cnd getDelFlag(Integer delFlag) {
        Cnd cnd = null != delFlag ? Cnd.where("del_flag", " = ", delFlag) : Cnd.where("del_flag", "<", Integer.valueOf(3));
        cnd.desc("create_at");
        return cnd;
    }

    @Override
    @Transactional(readOnly = false)
    public Object save(ClientPush clientPush) {
        Article article = articleService.fetch(clientPush.getArticleId());

        if (Lang.isEmpty(clientPush.getTitle())) {
            //标题为空则设置默认值
            clientPush.setTitle(article.getTitle());
        }

        Map<String, String> content = Maps.newConcurrentMap();
        content.put("title", clientPush.getTitle());
        content.put("desc", clientPush.getDescription());
        content.put("syscode", article.getSysCode());
        content.put("id", article.getArticleId().toString());
        content.put("pjCode", code);

        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);
        template.setTransmissionContent(jsonMapper.toJson(content));

        // 多厂商推送透传消息带 通知
       /* Notify notify = new Notify();
        notify.setTitle(clientPush.getTitle());
        notify.setContent(clientPush.getDescription());
        StringBuilder sbIntent = new StringBuilder();

        // intent参数
        sbIntent.append("intent:#Intent;package=com.shanxidaily.activity;component=com.shanxidaily.activity/com.peopletech.main.mvp.ui.activity.SplashActivity;");
        sbIntent.append("S.title=").append(clientPush.getTitle()).append(";");
        sbIntent.append("S.desc=").append(clientPush.getDescription()).append(";");
        sbIntent.append("S.syscode=").append(article.getSysCode()).append(";");
        sbIntent.append("S.id=").append(article.getArticleId().toString()).append(";");
        sbIntent.append("S.pjCode=").append(code).append(";");
        sbIntent.append("end");
        notify.setIntent(sbIntent.toString());
        notify.setType(GtReq.NotifyInfo.Type._intent);
        template.set3rdNotifyInfo(notify);//设置第三方通知*/

        AppConditions conditions = new AppConditions();
        List<String> phoneList = Lists.newArrayList();
        if (clientPush.getPlatform().equals(0)) {
            phoneList.add("ANDROID");
            phoneList.add("IOS");
        } else if (clientPush.getPlatform().equals(1)) {
            phoneList.add("IOS");
        } else if (clientPush.getPlatform().equals(2)) {
            phoneList.add("ANDROID");
        }
        if (phoneList.contains("IOS")) {
            //iOS推送需要在代码中通过TransmissionTemplate的setAPNInfo接口设置相应的APNs通知参数
            APNPayload payload = new APNPayload();
            payload.setContentAvailable(1);
            payload.setSound("default");
            payload.setAlertMsg(new APNPayload.SimpleAlertMsg(clientPush.getDescription()));
            payload.addCustomMsg("syscode", article.getSysCode());
            payload.addCustomMsg("id", article.getArticleId().toString());
            payload.addCustomMsg("pjCode", code);

            template.setAPNInfo(payload);
        }

        AppMessage message = new AppMessage();
        message.setAppIdList(Lists.newArrayList(appId));
        message.setOffline(true);
        message.setOfflineExpireTime(1000 * 600);
        conditions.addCondition(AppConditions.PHONE_TYPE, phoneList);
        message.setConditions(conditions);
        message.setData(template);

        IGtPush push = new IGtPush(url, appKey, masterSecret);
        IPushResult ret = push.pushMessageToApp(message);

        log.info(ret.getResponse().toString());
        clientPush.setResponseResult(ret.getResponse().toString());
        //推送状态，成功设为true
        if(null != ret){
            clientPush.setStatus(!"ok".equals(ret.getResponse().get("result")) ? false : true);
        }
        return super.save(clientPush);
    }


    @Transactional(readOnly = false)
    public int delete(Long id) {
        return this.vDelete(id);
    }

    @Override
    @Transactional(readOnly = false)
    public int updateIgnoreNull(ClientPush clientPush) {
        return super.updateIgnoreNull(clientPush);
    }

    /**
     * 根据语音识别获取信息查询消息推送列表
     * @param clientPushVO
     * @return
     */
    @Override
    public QueryResult searchPushInfo(ClientPushVO clientPushVO) {
        Cnd cnd = Cnd.where(ClientPush.Constant.DEL_FLAG, "<", Integer.valueOf(3));

        if(StringUtils.isNotBlank(clientPushVO.getTime())){
            Map<String,String> maps = AiuiTimeUtils.getAiuiTime(clientPushVO.getTime());
            Long startTime = DateHelper.getLongByString(maps.get("startTime"), "yyyy-MM-dd");
            Long endtime = DateHelper.getLongByString(maps.get("endTime"), "yyyy-MM-dd");
            if (startTime != null) {
                cnd.and(ClientPush.Constant.CREATE_AT, ">=", startTime);
            }
            if (endtime != null) {
                cnd.and(ClientPush.Constant.CREATE_AT, "<", endtime);
            }
        }

        String keyWords = clientPushVO.getKeyWords();
        if (!Lang.isEmpty(keyWords) && !"".equalsIgnoreCase(keyWords.trim())){
            cnd.and(Cnd.exps(ClientPush.Constant.TITLE, "like", "%" + keyWords + "%")
                    .or(ClientPush.Constant.DESCRIPTION, "like", "%" + keyWords + "%"));
        }
        cnd.desc(ClientPush.Constant.CREATE_AT);
        QueryResult queryResult = this.listPage(clientPushVO.getPageNumber(), clientPushVO.getPageSize(), cnd);
        List<ClientPush> list = (List<ClientPush>) queryResult.getList();
        if(!Lang.isEmpty(list) && list.size() > 0){
            list.stream().forEach(clientPush -> {
                Article article = articleService.fetch(clientPush.getArticleId());
                clientPush.setSysCode(article.getSysCode());
                clientPush.setType(article.getType());
            });
        }
        return queryResult;
    }
}
