package cn.people.one.modules.activitycode.service;

import cn.people.one.modules.activitycode.model.front.ActivityVO;
import org.nutz.dao.QueryResult;

/**
 * 邀请码service
 * Created by sunday on 2017/4/13.
 */
public interface IActivityCodeService {

    Object activityAdd(ActivityVO activity);

    QueryResult showActivityCode(Integer pageNumber,Integer pageSize,String type);

    QueryResult statActivityCode(Integer pageNumber,Integer pageSize);
}
