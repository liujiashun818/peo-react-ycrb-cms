package cn.people.one.modules.sys.service.impl;


import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.OrderBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.sys.model.BaseArea;
import cn.people.one.modules.sys.service.IBaseAreaService;


@Service
public class BaseAreaServiceImpl extends BaseService<BaseArea> implements IBaseAreaService
{
    private static Logger log = LoggerFactory.getLogger("BaseAreaServiceImpl");

    @Autowired
    private BaseDao dao;

    public List<BaseArea> queryAllBaseArea()
    {
        Criteria criteria = Cnd.NEW().getCri();
        List<BaseArea> list = dao.query(BaseArea.class, criteria);
        return list;
    }

    /**
     * select
     * id,code,province,city,district,parent,level,province_short,province_jiancheng,city_pinyin
     * from base_area where parent = #{pid} order by code asc
     */
    public List<BaseArea> queryBaseAreaByPid(String pid)
    {
        log.info("调用 >>>queryBaseAreaByPid(), 参数：{}", pid);
        Criteria criteria = Cnd.NEW().getCri();
        criteria.where().andEquals("parent", pid);
        OrderBy orderBy = criteria.getOrderBy();
        orderBy.asc("code");
        List<BaseArea> list = dao.query(BaseArea.class, criteria);
        return list;
    }
    
    /**
     *   select id,code,province,city,district,parent,level,province_short,province_jiancheng,city_pinyin
          from base_area where level =  #{level}
          order by code asc
     */

    public List<BaseArea> queryBaseAreaByLevel(String level)
    {
        log.info("调用 >>>queryBaseAreaByLevel(), 参数：{}", level);
        Criteria criteria = Cnd.NEW().getCri();
        criteria.where().andEquals("level", level);
        OrderBy orderBy = criteria.getOrderBy();
        orderBy.asc("code");
        List<BaseArea> list = dao.query(BaseArea.class, criteria);
        return list;
    }

}
