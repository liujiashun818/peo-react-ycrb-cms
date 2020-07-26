package cn.people.one.modules.live.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.live.model.LiveTalk;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
* 直播间的对话Service
* @author cheng
*/
public interface ILiveTalkService extends IBaseService<LiveTalk>{

    /**
     * 根据roomId查询对话列表
     */
    List<LiveTalk> findTalksByRoomId(Long roomId, Long time);

    List<LiveTalk> findTalksByRoomIdOnBrowser(Long roomId);

    /**
     * 发言
     */
    LiveTalk speak(LiveTalk talk,String message);

    /**
     * 回复
     */
    LiveTalk reply(LiveTalk talk);

    /**
     * 置顶
     */
    LiveTalk top(Long talkId, Long roomId);

    /**
     * 取消置顶
     */
    LiveTalk down(Long talkId);

    /**
     * 切换上下线状态
     */
    LiveTalk changeOnlineStatus(Long talkId);

    /**
     * 根据直播间id分页查询直播talk列表
     * @param articleId
     * @param size
     * @param page
     * @return
     */
    QueryResult findTalksByPage(Long articleId, Integer size, Integer page);

    List<LiveTalk> queryByCnd(Cnd condition);

    /**
     * 根据时间查询数据库中最新的直播间聊天数据以及置顶数据
     * @param articleId
     * @param time
     * @return
     */
    List<LiveTalk> findTalksByRoomIdTime(Long articleId, Long time);
}