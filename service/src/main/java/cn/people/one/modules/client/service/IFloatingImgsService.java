package cn.people.one.modules.client.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.client.model.FloatingImgs;
import cn.people.one.modules.client.model.front.FloatingImgsVO;
import org.nutz.dao.QueryResult;

/**
 * Created by sunday on 2018/9/25.
 */
public interface IFloatingImgsService extends IBaseService<FloatingImgs> {

    QueryResultVO<FloatingImgs> findSearchPage(Integer pageNumber, Integer pageSize, FloatingImgsVO floatingImgsVO);

    FloatingImgs changeOnlineStatus(Long id);

    FloatingImgs getLastFloatingImgs();

}
