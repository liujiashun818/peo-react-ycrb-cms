package cn.people.one.modules.live.service;

import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.live.socket.LiveTalkWebSocket;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 直播间 redis 订阅监听器，用于监听用户评论
 */
@Service
@Slf4j
public class LiveRedisReceiver {

    /**
     * 接收消息
     * @param message
     */
    public void receiveMessage(String message) {
        log.info("消息来了:{}", message);
        //推送数据到直播房间
        log.info("推送评论数据到直播房间");
        CopyOnWriteArraySet<LiveTalkWebSocket> sockets = LiveTalkWebSocket.getWebSocketSet();
        try {
            if(message.startsWith("\"") && message.endsWith("\"")){
                message=message.substring(1,message.length()-1);
                log.info("格式化后message："+message);
            }
            JSONObject jsonObject=JSONObject.parseObject(StringEscapeUtils.unescapeJava(message));
            if (null != sockets && sockets.size() > 0) {
                for (LiveTalkWebSocket socket : sockets) {
                    try {
                        synchronized (socket) {
                            socket.sendMessage(jsonObject.toJSONString());
                        }
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("推送评论数据到直播房间出错",e);
        }
        //这里是收到通道的消息之后执行的方法
    }
}
