package cn.people.one.modules.cms.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.cms.model.Revelations;
import org.nutz.dao.QueryResult;

public interface IRevelationsService extends IBaseService<Revelations>{
    QueryResultVO<Revelations> listPage(Integer pageNo, Integer pageSize);
    QueryResultVO<Revelations> findListByName(Integer pageNumber, Integer pageSize, String name);
    void saveRevelations(Revelations revelations);

    /**
     * 根据id查询新闻爆料详情
     * @param id
     * @return
     */
    Revelations getRevelationsById(Long id);
}
