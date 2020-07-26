package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.Site;
import cn.people.one.modules.cms.service.ISiteService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lml on 2016/12/26.
 */
@Transactional(readOnly = true)
@Service
public class SiteService extends BaseService<Site> implements ISiteService {
    @Autowired
    private BaseDao dao;

    @Override
    public QueryResult find(Integer pageNo, Integer pageSize, Site site) {
        Cnd cnd = null;
        if (StringUtils.isNotBlank(site.getName())) {
            cnd = Cnd.where("name", "like", "%" + site.getName() + "%");
        }
        return this.listPage(pageNo, pageSize, cnd);
    }

    @Override
    @Transactional(readOnly = false)
    public Object save(Site site) {
        if (StringUtils.isNotBlank(site.getCopyright())) {
            site.setCopyright(StringEscapeUtils.unescapeHtml4(site.getCopyright()));
        }
        return super.save(site);
    }

}
