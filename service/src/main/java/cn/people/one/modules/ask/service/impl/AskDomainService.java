package cn.people.one.modules.ask.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.ask.model.AskDomain;
import cn.people.one.modules.ask.model.AskDomainResp;
import cn.people.one.modules.ask.service.IAskDomainService;
import cn.people.one.modules.base.service.impl.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class AskDomainService extends BaseService<AskDomain> implements IAskDomainService {
    /** 
    * 全部问题领域，缓存key
    */ 
    private static final String ALL_DOMAIN = "all_domain";
    private static final String ASKDOMAIN_ID_NAME = "all_id_and_name";
    Map<String, Object> cache=new ConcurrentHashMap<>();
    @Autowired
    BaseDao dao;
    @Override
    public AskDomain getDomainByid(int id)
    {   
        if(cache.containsKey(id+"")) {
           return (AskDomain)cache.get(id+"");
        }
        AskDomain domain= dao.fetch(tClass, id);
        if(domain!=null) {
            cache.put(id+"", domain);
        }
        return domain;
    }
    @Override
    public List<AskDomain> getAllDomains()
    {
        if(cache.containsKey(ALL_DOMAIN)) {
            return (List<AskDomain>)cache.get(ALL_DOMAIN);
        }
        List<AskDomain> list= dao.query(tClass, Cnd.NEW().getOrderBy().desc("sort"));
        cache.put(ALL_DOMAIN, list);
        return list;
    }
    @Override
    public String getNameByid(int id)
    {
        AskDomain d=getDomainByid(id);
        if(d==null) {
            return "";
        }
        return d.getName();
    }
    @Override
    public void clearCache()
    {
       cache.clear();
        
    }

    @Override
    public List<AskDomainResp> getAllDomainsIdAndName()    {
//        if(cache.containsKey(ASKDOMAIN_ID_NAME)) {
//            return (List<AskDomainResp>)cache.get(ASKDOMAIN_ID_NAME);
//        }

        Sql sql = Sqls.create("SELECT id,name FROM ask_domain where del_flag is null and is_use=1");
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(AskDomainResp.class));
        dao.execute(sql);
        List<AskDomainResp> list = sql.getList(AskDomainResp.class);

//        cache.put(ASKDOMAIN_ID_NAME, list);
        return list;
    }

}