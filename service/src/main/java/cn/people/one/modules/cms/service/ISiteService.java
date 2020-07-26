package cn.people.one.modules.cms.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.cms.model.Site;
import org.nutz.dao.QueryResult;

/**
 * Created by lml on 2016/12/26.
 */
public interface ISiteService extends IBaseService<Site> {

    QueryResult find(Integer pageNo, Integer pageSize, Site site);

}
