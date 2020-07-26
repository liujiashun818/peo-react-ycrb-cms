package cn.people.one.modules.client.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.client.model.LoadingImgs;
import cn.people.one.modules.client.model.front.LoadingImgsVO;
import org.nutz.dao.QueryResult;

/**
 * Created by sunday on 2017/4/12.
 */
public interface ILoadingImgsService extends IBaseService<LoadingImgs> {

    QueryResultVO<LoadingImgs> findSearchPage(Integer pageNumber, Integer pageSize, LoadingImgsVO loadingImgsVO);

    LoadingImgs changeOnlineStatus(Long id);

    void sendLoadingImgs();

    LoadingImgs getLastLoadingImgs();
}
