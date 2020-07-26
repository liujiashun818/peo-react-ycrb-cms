package cn.people.one.modules.live.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.modules.cms.message.SendMessage;
import cn.people.one.modules.cms.model.front.ArticleMediaVO;
import cn.people.one.modules.cms.model.front.LiveTalkMediaVO;
import cn.people.one.modules.comment.model.Comments;
import cn.people.one.modules.live.model.LiveRoom;
import cn.people.one.modules.live.model.LiveTalk;
import cn.people.one.modules.live.model.LiveUser;
import cn.people.one.modules.live.service.ILiveTalkService;
import com.google.gson.Gson;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
* 直播间的对话Service
* @author cheng
*/
@Transactional(readOnly = true)
@Service
public class LiveTalkService extends BaseService<LiveTalk> implements ILiveTalkService {

    @Autowired
    private BaseDao dao;

    @Override
    public List<LiveTalk> findTalksByRoomId(Long roomId, Long time) {
        Criteria cri = Cnd.cri();
        cri.where().andEquals("room_id", roomId).andEquals("parent_id", 0).andGT("update_at", time);
        if(time.equals(0)){
            cri.where().andEquals("del_flag",0);
        }else{
            cri.where().andLTE("del_flag",3);
        }
        cri.getOrderBy().desc("update_at");
        List<LiveTalk> talks = dao.query(LiveTalk.class, cri);
        talks.stream().forEach(t -> {
            Condition condition1 = Cnd.where("parent_id", "=", t.getId()).and("room_id", "=", roomId).and("update_at", ">=", time).and("del_flag", "=", 0).desc("update_at");
            List<LiveTalk> reply = dao.query(LiveTalk.class, condition1);
            if(!Lang.isEmpty(reply)){
                t.setLiveReply(reply.get(0));
            }
        });
        return talks;
    }

    @Override
    public List<LiveTalk> findTalksByRoomIdOnBrowser(Long roomId) {
        Condition condition = Cnd.where("room_id", "=", roomId).and("parent_id", "=", 0).and("del_flag", "<", 3).desc("create_at");
        List<LiveTalk> talks = dao.query(LiveTalk.class, condition);
        talks.stream().forEach(t -> {
            Condition condition1 = Cnd.where("room_id", "=", roomId).and("parent_id", "=", t.getId()).and("del_flag", "<", 3).desc("create_at");
            List<LiveTalk> reply = dao.query(LiveTalk.class, condition1);
            if(!Lang.isEmpty(reply)){
                t.setLiveReply(reply.get(0));
            }
            t.setMedia(new Gson().fromJson(t.getMediaJson(), LiveTalkMediaVO.class));
        });
        return talks;
    }


    @Override
    @Transactional
    public LiveTalk speak(LiveTalk talk,String message) {
        if(Lang.isEmpty(talk.getRoomId())){
            return null;
        }
        talk.init();
        talk.setParentId(0L);
        //处理媒体资源
        if(talk.getMedia()!=null){
            JsonMapper jsonMapper =new JsonMapper();
            talk.setMediaJson(jsonMapper.toJson(talk.getMedia()));
        }
        LiveTalk talk1 = dao.insert(talk);
        SendMessage.sendLiveTalk(talk1.getRoomId());
        return talk1;
    }

    @Override
    @Transactional
    public LiveTalk reply(LiveTalk talk) {
        if(Lang.isEmpty(talk.getCommentId()) || Lang.isEmpty(talk.getRoomId())){
            return null;
        }
        talk.init();
        talk.setParentId(0L);
        dao.insert(talk);
        Comments comments = dao.fetch(Comments.class, talk.getCommentId());
        LiveTalk reply = BeanMapper.map(comments, LiveTalk.class);
        reply.setParentId(talk.getId());
        reply.setRoomId(talk.getRoomId());
        reply.setUserType("net");
        reply.setTop(false);
        dao.insert(reply);
        talk.setLiveReply(reply);
        SendMessage.sendLiveTalk(talk.getRoomId());
        return talk;
    }

    @Override
    @Transactional
    public LiveTalk top(Long talkId, Long roomId) {
        //1.根据roomId查询此直播间下所有的对话
        List<LiveTalk> talks = dao.query(LiveTalk.class, Cnd.where("del_flag", "<", BaseEntity.STATUS_DELETE).and("room_id", "=", roomId));
        if(Lang.isEmpty(talks)){
            return null;
        }
        talks.stream().forEach(t -> {
            if(t.getTop()){
                t.setTop(false);
                t.setUpdateAt(System.currentTimeMillis());
                dao.updateIgnoreNull(t);
            }
        });
        LiveTalk topTalk = dao.fetch(LiveTalk.class, talkId);
        topTalk.setTop(true);
        topTalk.setUpdateAt(System.currentTimeMillis());
        dao.updateIgnoreNull(topTalk);
        Condition condition = Cnd.where("parent_id", "=", talkId).and("del_flag", "<", 3).desc("create_at");
        List<LiveTalk> reply = dao.query(LiveTalk.class, condition);
        if(!Lang.isEmpty(reply)){
            topTalk.setLiveReply(reply.get(0));
        }
        topTalk.setMedia(new Gson().fromJson(topTalk.getMediaJson(), LiveTalkMediaVO.class));
        SendMessage.sendLiveTalk(roomId);
        return topTalk;
    }

    @Override
    @Transactional
    public LiveTalk down(Long talkId) {
        LiveTalk topTalk = dao.fetch(LiveTalk.class, talkId);
        topTalk.setTop(false);
        topTalk.setUpdateAt(System.currentTimeMillis());
        dao.updateIgnoreNull(topTalk);
        Condition condition = Cnd.where("parent_id", "=", talkId).and("del_flag", "<", 3).desc("create_at");
        List<LiveTalk> reply = dao.query(LiveTalk.class, condition);
        if(!Lang.isEmpty(reply)){
            topTalk.setLiveReply(reply.get(0));
        }
        topTalk.setMedia(new Gson().fromJson(topTalk.getMediaJson(), LiveTalkMediaVO.class));
        SendMessage.sendTalkDown(topTalk);
        return topTalk;
    }

    @Override
    @Transactional
    public LiveTalk changeOnlineStatus(Long talkId) {
        LiveTalk talk = dao.fetch(LiveTalk.class, talkId);
        if (talk.getDelFlag().equals(BaseEntity.STATUS_OFFLINE) || talk.getDelFlag().equals(BaseEntity.STATUS_AUDIT)) {
            talk.setDelFlag(BaseEntity.STATUS_ONLINE);
        } else {
            talk.setDelFlag(BaseEntity.STATUS_OFFLINE);
        }
        talk.setUpdateAt(System.currentTimeMillis());
        dao.updateIgnoreNull(talk);
        Condition condition = Cnd.where("parent_id", "=", talkId).and("del_flag", "<", 3).desc("create_at");
        List<LiveTalk> reply = dao.query(LiveTalk.class, condition);
        if(!Lang.isEmpty(reply)){
            talk.setLiveReply(reply.get(0));
        }
        talk.setMedia(new Gson().fromJson(talk.getMediaJson(), LiveTalkMediaVO.class));
        SendMessage.sendLiveTalk(talk.getRoomId());
        return talk;
    }

    /**
     * 根据直播间id分页查询直播talk列表
     * @param roomId
     * @param size
     * @param page
     * @return
     */
    @Override
    public QueryResult findTalksByPage(Long roomId, Integer size, Integer page) {
        Criteria cri = Cnd.cri();
        cri.where().andEquals("room_id", roomId).andEquals("parent_id", 0).andEquals("del_flag",0);
        cri.getOrderBy().desc("top").desc("create_at");
        QueryResult queryResult=this.listPage(page,size,cri);
        List<LiveTalk> talks = (List<LiveTalk>) queryResult.getList();
        talks.stream().forEach(t -> {
            Condition condition1 = Cnd.where("parent_id", "=", t.getId()).
                    and("room_id", "=", roomId).
                    and("del_flag", "=", 0).desc("create_at");
            List<LiveTalk> reply = dao.query(LiveTalk.class, condition1);
            if(!Lang.isEmpty(reply)){
                t.setLiveReply(reply.get(0));
            }
        });
        queryResult.setList(talks);
        return queryResult;
    }

    @Override
    public List<LiveTalk> queryByCnd(Cnd condition) {
        return dao.query(LiveTalk.class,condition);
    }

    /**
     * 根据时间查询数据库中最新的直播间聊天数据以及置顶数据
     * @param roomId 直播间id
     * @param time
     * @return
     */
    @Override
    public List<LiveTalk> findTalksByRoomIdTime(Long roomId, Long time) {
        Cnd cnd=Cnd.where("room_id","=",roomId).and("parent_id","=",0)
                .and("del_flag","=",LiveTalk.STATUS_ONLINE)
                .and(Cnd.exps("update_at",">",time).or("top","=",1));
        cnd.getOrderBy().desc("top").desc("create_at");
        List<LiveTalk> talks = dao.query(LiveTalk.class, cnd);
        talks.stream().forEach(t -> {
            Condition condition1 = Cnd.where("parent_id", "=", t.getId()).
                    and("del_flag", "=", 0).
                    and("room_id", "=", roomId).
                    desc("create_at");
            List<LiveTalk> reply = dao.query(LiveTalk.class, condition1);
            if(!Lang.isEmpty(reply)){
                t.setLiveReply(reply.get(0));
            }
        });
        return talks;
    }
}