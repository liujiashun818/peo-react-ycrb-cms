package cn.people.one.modules.live.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.live.model.LiveRoom;

/**
 * 直播间Service
 *
 * @author cheng
 */
public interface ILiveRoomService extends IBaseService<LiveRoom> {

    /**
     * redis channel 直播间评论
     */
    static final String REDIS_CHANNEL_LIVE_ROOM_COMMENT = "CHANNEL_LIVE_ROOM_COMMENT";

    /**
     * 直播保存
     */
    Long insertLiveRoom(LiveRoom room);

    /**
     * 直播更新
     */
    Long updateLiveRoom(LiveRoom room);

    /**
     * 客户端上根据id查询直播信息
     */
    LiveRoom fetchByApp(Long id);

    /**
     * 自动将预告时间到的直播更新为直播中状态
     */
    void autoUpdatePrevueToLiveing();

    int incrementHits(Long liveId);

}