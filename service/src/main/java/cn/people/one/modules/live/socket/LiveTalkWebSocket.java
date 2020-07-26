package cn.people.one.modules.live.socket;

import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.comment.model.Comments;
import cn.people.one.modules.comment.service.ICommentsService;
import cn.people.one.modules.live.config.SpringUtil;
import cn.people.one.modules.live.model.LiveTalk;
import cn.people.one.modules.live.service.ILiveTalkService;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.lang.Lang;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * User: 张新征
 * Date: 2017/3/26 17:07
 * Description:
 */
@Component
@ServerEndpoint("/api/live/talk/{roomId}")
@Slf4j
public class LiveTalkWebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<LiveTalkWebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    public static CopyOnWriteArraySet<LiveTalkWebSocket> getWebSocketSet() {
        log.info("当前连接数为" + webSocketSet.size());
        return webSocketSet;
    }

    private Long roomId;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "roomId") Long roomId) throws IOException {
        this.session = session;
        this.setRoomId(roomId);
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        log.info("有新连接加入！当前连接数为" + getOnlineCount() + "---- 直播房间id为：" + this.getRoomId());
        ILiveTalkService talkService = (ILiveTalkService) SpringUtil.getBean("liveTalkService");
        ICommentsService commentsService = (ICommentsService) SpringUtil.getBean("commentsService");
        List<LiveTalk> talks = talkService.findTalksByRoomIdOnBrowser(roomId);
        List<Comments> comments = commentsService.comments(SysCodeType.LIVE.value(), roomId);
        Map<String, Object> map = Maps.newHashMap();
        map.put("method", "init");
        map.put("talks", talks);
        map.put("comments", comments);
        this.sendMessage(new Gson().toJson(map));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前连接数为" + getOnlineCount() + "---- 关闭的直播房间id为：" + this.getRoomId());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        if(StringUtils.isBlank(message)){
            for (LiveTalkWebSocket socket : webSocketSet) {
                if (socket.getRoomId().equals(roomId)) {
                    Map<String,Object> map = Maps.newHashMap();
                    map.put("method", "poll");
                    socket.sendMessage(new Gson().toJson(map));
                }
            }
            return;
        }
        ILiveTalkService talkService = (ILiveTalkService) SpringUtil.getBean("liveTalkService");
        ICommentsService commentsService = (ICommentsService) SpringUtil.getBean("commentsService");
        LiveTalk talk = new Gson().fromJson(message, LiveTalk.class);
        if (talk.getMethod().equals("speak")) {
            talkService.speak(talk,message);
        } else if (talk.getMethod().equals("reply")) {
            talkService.reply(talk);
        } else if (talk.getMethod().equals("top")) {
            talk = talkService.top(talk.getId(), this.getRoomId());
            talk.setMethod("top");
        } else if (talk.getMethod().equals("down")) {
            talk = talkService.down(talk.getId());
            talk.setMethod("down");
        } else if (talk.getMethod().equals("statusTalk")) {
            talk = talkService.changeOnlineStatus(talk.getId());
            talk.setMethod("statusTalk");
        } else if (talk.getMethod().equals("statusComment")) {
            commentsService.changeOnlineStatus(talk.getId());
            Comments comments = commentsService.fetch(talk.getId());
            if(!Lang.isEmpty(comments)){
                talk.setDelFlag(comments.getDelFlag());
            }
        }
        for (LiveTalkWebSocket socket : webSocketSet) {
            if (socket.getRoomId().equals(roomId)) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("method", talk.getMethod());
                map.put("talk", talk);
                socket.sendMessage(new Gson().toJson(map));
            }
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.info("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        LiveTalkWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        LiveTalkWebSocket.onlineCount--;
    }
}
