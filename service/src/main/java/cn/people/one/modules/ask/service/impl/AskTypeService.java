package cn.people.one.modules.ask.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.ask.model.AskDomainResp;
import cn.people.one.modules.ask.model.AskType;
import cn.people.one.modules.ask.service.IAskTypeService;
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
public class AskTypeService extends BaseService<AskType> implements IAskTypeService {

    /** 
    * 所有类型的key
    */ 
    private static final String ALL_ASK_TYPES = "all_ask_types";
    private static final String ASKTYPE_ID_NAME = "all_id_and_name";
    @Autowired
    BaseDao dao;
    Map<String, Object> cache=new ConcurrentHashMap<>();
    
    @Override
    public AskType getTypeByid(int typeId)
    {
        if(cache.containsKey(typeId+"")) {
          return  (AskType)cache.get(typeId+""); 
        }
        AskType type=dao.fetch(tClass, typeId);
        if(type!=null) {
            cache.put(typeId+"", type);
        }
       
        return type;
    }

    @Override
    public List<AskType> getAllTypes()
    {
        if(cache.containsKey(ALL_ASK_TYPES)) {
            return  (List<AskType>)cache.get(ALL_ASK_TYPES); 
          }
          List<AskType> type=dao.query(tClass, Cnd.NEW().getOrderBy().desc("sort"));
          cache.put(ALL_ASK_TYPES, type);
          return type;
    }

    @Override
    public String getNameByid(int typeId)
    {
        AskType t=getTypeByid(typeId);
        if(t==null) {
            return "";
        }
        return t.getName();
    }
    @Override
    public void clearCache()
    {
        cache.clear();
        
    }

    @Override
    public List<AskDomainResp> getAllDomainsIdAndName()
    {
//        if(cache.containsKey(ASKTYPE_ID_NAME)) {
//            return (List<AskDomainResp>)cache.get(ASKTYPE_ID_NAME);
//        }

        Sql sql = Sqls.create("SELECT id,name FROM ask_type where del_flag is null");
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(AskDomainResp.class));
        dao.execute(sql);
        List<AskDomainResp> list = sql.getList(AskDomainResp.class);

//        cache.put(ASKTYPE_ID_NAME, list);
        return list;
    }
}