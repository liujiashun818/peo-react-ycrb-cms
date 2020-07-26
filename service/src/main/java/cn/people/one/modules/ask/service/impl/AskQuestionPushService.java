package cn.people.one.modules.ask.service.impl;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.ask.model.AskQuestionPush;
import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.ask.service.IAskQuestionPushService;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.client.model.ClientPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@EnableAsync
public class AskQuestionPushService extends BaseService<AskQuestionPush> implements IAskQuestionPushService{
    private static JsonMapper jsonMapper = JsonMapper.defaultMapper();

    private static final String CONTENT="您提出的问题已经收到回复了，快去看看吧";
    @Value("${gexin.appId}")
    private String appId;

    @Value("${gexin.appKey}")
    private String appKey;

    @Value("${gexin.masterSecret}")
    private String masterSecret;

    @Value("${gexin.url}")
    private String url;
    /**
     * 保存问政推送
     * @param askQuestionReply
     */
    @Transactional
    @Override
    public Object savePushContent(AskQuestionReply askQuestionReply) {
        AskQuestionReply askQuestionReply1=dao.fetch(AskQuestionReply.class,askQuestionReply.getId());

        AskQuestionPush askQuestionPush=new AskQuestionPush();
        askQuestionPush.setAskId(askQuestionReply1.getId());
        askQuestionPush.setPushContent(CONTENT);
        askQuestionPush.setTitle(askQuestionReply1.getTitle());
        askQuestionPush.setUdid(askQuestionReply1.getUdid());

        Map<String, String> content = Maps.newConcurrentMap();
        content.put("title", askQuestionReply.getTitle());
        content.put("desc", CONTENT);
        content.put("syscode", "ask");
        content.put("id", askQuestionReply.getId().toString());
        content.put("pjCode", "code_ycrb");
        content.put("type", "ask");

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
        phoneList.add("ANDROID");
        phoneList.add("IOS");
        if (phoneList.contains("IOS")) {
            //iOS推送需要在代码中通过TransmissionTemplate的setAPNInfo接口设置相应的APNs通知参数
            APNPayload payload = new APNPayload();
            payload.setContentAvailable(1);
            payload.setSound("default");
            payload.setAlertMsg(new APNPayload.SimpleAlertMsg(CONTENT));
            payload.addCustomMsg("syscode", "ask");
            payload.addCustomMsg("id", askQuestionReply.getId().toString());
            payload.addCustomMsg("pjCode", "code_ycrb");
            template.setAPNInfo(payload);
        }

        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        message.setOfflineExpireTime(1000 * 600);
        conditions.addCondition(AppConditions.PHONE_TYPE, phoneList);
        message.setData(template);
        Target target=new Target();
        target.setAppId(appId);
        target.setClientId(askQuestionReply1.getPushid());
        IGtPush push = new IGtPush(url, appKey, masterSecret);
        IPushResult ret = push.pushMessageToSingle(message, target);

        log.info(ret.getResponse().toString());
        askQuestionPush.setResponseResult(ret.getResponse().toString());
        //推送状态，成功设为true
        if(null != ret){
            askQuestionPush.setStatus(!"ok".equals(ret.getResponse().get("result")) ? false : true);
        }
        return super.save(askQuestionPush);
    }
}
