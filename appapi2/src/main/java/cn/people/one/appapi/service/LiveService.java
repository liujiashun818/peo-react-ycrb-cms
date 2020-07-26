package cn.people.one.appapi.service;

import cn.people.one.appapi.vo.*;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.cms.model.front.LiveTalkMediaVO;
import cn.people.one.modules.live.model.LiveRoom;
import cn.people.one.modules.live.model.LiveTalk;
import cn.people.one.modules.live.service.ILiveRoomService;
import cn.people.one.modules.live.service.ILiveTalkService;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author YX
 * @date 2018/10/15
 * @comment
 */
@Service
public class LiveService {

    @Value("${http.vshare}")
    private String url;

    @Autowired
    private ILiveRoomService liveRoomService;

    @Autowired
    private ILiveTalkService liveTalkService;

    /**
     * 获取直播详情
     *
     * @param articleId
     * @return
     */
    public LiveRoomVO getLiveRoomDetail(Long articleId) {
        LiveRoom liveRoom = liveRoomService.fetchByApp(articleId);
        if (liveRoom == null) {
            return null;
        }

        LiveRoomVO liveRoomVO = BeanMapper.map(liveRoom, LiveRoomVO.class);
        if (StringUtils.isNotBlank(liveRoom.getStatus())) {
            if ((liveRoom.getStatus().equals(LiveRoom.LIVE_STATUS_PREVUE) || liveRoom.getStatus().equals(LiveRoom.LIVE_STATUS_LIVEING)) && StringUtils.isNotBlank(liveRoom.getVideo())) {
                liveRoomVO.setVideo(liveRoom.getVideo());
            }
            if (liveRoom.getStatus().equals(LiveRoom.LIVE_STATUS_FINISHED) && StringUtils.isNotBlank(liveRoom.getPlayback())) {
                liveRoomVO.setVideo(liveRoom.getPlayback());
            }
        }
        liveRoomVO.setDate(liveRoom.getCreateAt() != null ? new Date(liveRoom.getCreateAt()) : liveRoom.getLiveTime());
        if (!Lang.isEmpty(liveRoom.getGuests())) {
            liveRoomVO.setGuests(BeanMapper.mapList(liveRoom.getGuests(), LiveUserVO.class));
        }
        if (!url.endsWith("/")) {
            url += "/";
        }
        liveRoomVO.setShareUrl(url + "live/" + liveRoomVO.getId());

        liveRoomVO.setHits(liveRoomService.incrementHits(articleId));

        return liveRoomVO;
    }

    /**
     * 获取直播间的聊天和回复信息
     *
     * @param articleId 直播id
     * @param size      每页显示条数
     * @param page      当前页
     * @return
     */
    public ResultVO getLiveRoomTalk(Long articleId, Integer size, Integer page) {
        //TODO 取缓存
        //1、获取直播间状态和访客人数
        Map resultMap = getLiveRoomInfo(articleId);
        resultMap.put("time", 0);
        //2、根据直播id，分页过滤出直播聊天数据
        QueryResult queryResult = liveTalkService.findTalksByPage(articleId, size, page);
        List<LiveTalk> liveTalkList = (List<LiveTalk>) queryResult.getList();
        if (liveTalkList == null || liveTalkList.size() == 0) {
            liveTalkList = new ArrayList<>();
            return new ResultVO(resultMap, liveTalkList);
        }
        //3、处理列表数据，组装成返回客户端的数据结构
        List<LiveTalkVO> liveTalkVOList = Lists.newArrayList();
        Long time = handleLiveTalkList(liveTalkList, liveTalkVOList);
        if(!Lang.isEmpty(time)){
            resultMap.put("time", time);
        }
        queryResult.setList(liveTalkVOList);
        return new ResultVO(resultMap, queryResult);
    }

    /**
     * 获取最新直播间列表数据
     *
     * @param articleId 直播间id
     * @param time      更新时间 时间戳
     * @return
     */
    public ResultVO5<Map, LiveTalkVO> getNewLiveRoomTalk(Long articleId, Long time) {
        //1、获取直播间状态和访客人数
        Map itemMap = getLiveRoomInfo(articleId);
        itemMap.put("time", time==null?0:time);
        //2、根据直播id，时间戳过滤出直播聊天数据（包含置顶数据）
        List<LiveTalk> liveTalkList = liveTalkService.findTalksByRoomIdTime(articleId, time);
        if (liveTalkList == null || liveTalkList.size() == 0) {
            liveTalkList = new ArrayList<>();
            return ResultVO5.success(itemMap,new ArrayList<LiveTalkVO>(),0,0,0);
        }
        //2、处理列表数据，组装成返回客户端的数据结构
        List<LiveTalkVO> liveTalkVOList = Lists.newArrayList();
        Long timeNew = handleLiveTalkList(liveTalkList, liveTalkVOList);
        if(!Lang.isEmpty(timeNew)){
            itemMap.put("time", timeNew >= time ? timeNew : time);
        }
        return ResultVO5.success(itemMap,liveTalkVOList,0,0,0);
    }

    /**
     * 处理直播间聊天列表数据，组装返回客户端数据
     *
     * @param liveTalkList   数据库查询出的直播间列表数据
     * @param liveTalkVOList 返回客户端的封装数据
     */
    private Long handleLiveTalkList(List<LiveTalk> liveTalkList, List<LiveTalkVO> liveTalkVOList) {
        List<Long> times = Lists.newArrayList();
        for (LiveTalk liveTalk : liveTalkList) {
            if (!Lang.isEmpty(liveTalk.getUpdateAt())) {
                liveTalk.setTime((liveTalk.getUpdateAt()));
                times.add(liveTalk.getUpdateAt());
            }
            LiveTalkVO liveTalkVO = BeanMapper.map(liveTalk, LiveTalkVO.class);
            if(StringUtils.isNotBlank(liveTalk.getMediaJson())){
                liveTalkVO.setMedias(new Gson().fromJson(liveTalk.getMediaJson(), LiveTalkMediaVO.class));
            }
            liveTalkVO.setTime(liveTalk.getUpdateAt());
            liveTalkVO.setDate(new Date(liveTalk.getCreateAt()));
            if (!Lang.isEmpty(liveTalk.getLiveReply())) {
                LiveTalkReplyVO liveTalkReplyVO = BeanMapper.map(liveTalk.getLiveReply(), LiveTalkReplyVO.class);
                liveTalkReplyVO.setTime(liveTalk.getLiveReply().getUpdateAt());
                liveTalkReplyVO.setDate(new Date(liveTalk.getLiveReply().getCreateAt()));
                if (StringUtils.isNotBlank(liveTalk.getLiveReply().getOpenId())) {
                    liveTalkReplyVO.setUserOpenId(liveTalk.getLiveReply().getOpenId());
                }
                liveTalkVO.setReplies(Lists.newArrayList(liveTalkReplyVO));
            }
            if (StringUtils.isNotBlank(liveTalk.getOpenId())) {
                liveTalkVO.setUserOpenId(liveTalk.getOpenId());
            }
            liveTalkVOList.add(liveTalkVO);
        }
        return times.size()>0?Collections.max(times):0L;
    }


    /**
     * 根据直播间id获取直播间参与人数和直播状态
     *
     * @param articleId
     * @return
     */
    private Map<String, Object> getLiveRoomInfo(Long articleId) {
        int hits = 0;
        String status = null;
        Map resultMap = new HashMap();
        LiveRoom liveRoom = liveRoomService.fetch(articleId);
        if (liveRoom != null) {
            status = liveRoom.getStatus();
            if (StringUtils.isNotBlank(liveRoom.getGuestIds())) {
                String guestIdArry[] = liveRoom.getGuestIds().split(",");
                hits = guestIdArry.length;
            }
        }
        resultMap.put("hits", hits);
        resultMap.put("status", status);
        return resultMap;
    }
}
