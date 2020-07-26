package cn.people.one.modules.live.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.live.model.LiveUser;
import cn.people.one.modules.live.service.ILiveUserService;
import org.nutz.dao.Cnd;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* 直播嘉宾/主持人Service
* @author cheng
*/
@Service
public class LiveUserService extends BaseService<LiveUser> implements ILiveUserService {

    @Autowired
    private BaseDao dao;

    @Override
    public List<LiveUser> list(String role) {
        List<LiveUser> list = dao.query(LiveUser.class, Cnd.where("role", "=", role));
        if(Lang.isEmpty(list)){
            return null;
        }
        return list;
    }
}