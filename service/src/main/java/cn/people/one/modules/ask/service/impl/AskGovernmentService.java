package cn.people.one.modules.ask.service.impl;


import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.modules.ask.model.AskGovernment;
import cn.people.one.modules.ask.model.AskGovernmentType;
import cn.people.one.modules.ask.model.front.AskGovernmentVO;
import cn.people.one.modules.ask.service.IAskGovernmentService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.Sql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
public class AskGovernmentService extends BaseService<AskGovernment> implements IAskGovernmentService
{
    /**
     * 所有机构的缓存key
     */
    private static final String ALL_GOV = "all_gov";
    private static final String ALL_GOV_AREA = "all_gov_area";
    private static final String ALL_CITY_AREA = "all_city_area";
    private static final String PARENT_GOVID = "34";
    private static final long PARENT_FETCHGOVID = 34;
    private static final String CHILD_GOVID = "426";

    @Autowired
    BaseDao dao;

    Map<String, Object> cache=new ConcurrentHashMap<>();

    @Override
    public List<AskGovernmentVO> getAllLocalGov()
    {
        if(cache.containsKey(ALL_GOV)) {
            return (List<AskGovernmentVO>)cache.get(ALL_GOV);
        }

        Criteria criteria = Cnd.NEW().getCri();
        criteria.where().andEquals("is_local_gov", 1).andIn("id",PARENT_GOVID,CHILD_GOVID);
        List<AskGovernment> list=dao.query(tClass, criteria);
        List<AskGovernment> resultList=new ArrayList<>();
        //组装假的组顶级的机构
        AskGovernment gen=dao.fetch(AskGovernment.class,PARENT_FETCHGOVID);
        resultList.add(gen);
        for(AskGovernment askGovernment:resultList){
            askGovernment.setChild(getChild(askGovernment.getFid(),list));
        }
        List<AskGovernmentVO> askGovernmentVOList=BeanMapper.mapList(resultList, AskGovernmentVO.class);
        cache.put(ALL_GOV, askGovernmentVOList);
        return askGovernmentVOList;
    }
    private List<AskGovernment> getChild(Long id, List<AskGovernment> list) {
        List<AskGovernment> childList = new ArrayList<>();
        for (AskGovernment askGovernment : list) {
            // 遍历所有节点，将FUP和Fid进行比较
            if (askGovernment.getFup()==id) {
                childList.add(askGovernment);
            }
        }
        // 继续遍历子栏目
        for (AskGovernment askGovernment : childList) {
            askGovernment.setChild(getChild(askGovernment.getFid(), list));
        }
        // 判断递归结束
        if (childList.size() == 0) {
            return null;
        }
        return childList;

    }
    @Override
    public List<Long> traverseChild(Long fid)
    {   String key="traverseChild"+fid;
        if(cache.containsKey(key)) {
            return (List<Long>)cache.get(key);
        }
        List<Long> list=recursionFetch(fid);
        cache.put(key, list);
        return list;
    }

    private List<Long> recursionFetch(Long fid){
        List<Long> childIdList=new ArrayList<>();
        if(fid<=0) {
            return new ArrayList<>();
        }
//       if(clearList) {
//           childIdList.clear();
//       }
        Criteria criteria = Cnd.NEW().getCri();
        criteria.where().andEquals("fup", fid);
        criteria.getOrderBy().asc("sort");
        List<AskGovernment> askGovs = dao.query(tClass,criteria);
        ArrayList<AskGovernment> childList= new ArrayList<AskGovernment>();
        for(AskGovernment askGov: askGovs){
            if(askGov.isLocalGov() && askGov.getHasChild()){
                childList.add(askGov);
            }else{
                childIdList.add(askGov.getFid());
            }
        }
        if(childList.size()>0){
            for(AskGovernment ask:childList){
                childIdList.addAll(recursionFetch(ask.getFid())) ;
            }
        }
        return childIdList;
    };

    @Override
    public AskGovernment getGovernmentById(Long id)
    {
        if(cache.containsKey(id+"")) {
            return (AskGovernment)cache.get(id+"");
        }
        AskGovernment askGovernment= dao.fetch(tClass,id);
        if(askGovernment!=null) {
            cache.put(id+"", askGovernment);
        }
        return askGovernment;
    }
    @Override
    public AskGovernment getGovernmentByFup(Long id)
    {
        if(cache.containsKey(id+"")) {
            return (AskGovernment)cache.get(id+"");
        }
        AskGovernment askGovernment= dao.fetch(tClass,Cnd.where("fup","=","0"));
        if(askGovernment!=null) {
            cache.put(id+"", askGovernment);
        }
        return askGovernment;
    }
    @Override
    public void clearCache()
    {
        cache.clear();

    }
    @Override
    public String getNameById(Long id)
    {
        AskGovernment gov=getGovernmentById(id);
        if(gov==null) {
            return "";
        }
        return gov.getName();
    }

    @Override
    public List<AskGovernmentType> getAllArea(Long gid) {

        List<AskGovernmentType> list = null;//返回结果

        Sql sql = null;
        if(gid == null || 0 == gid){ //空或0获取全部省级地区
//            if(cache.containsKey(ALL_GOV_AREA)) {
//                return (List<AskGovernmentType>)cache.get(ALL_GOV_AREA);
//            }
//
            sql = Sqls.create("SELECT id,fid,name,area_code,has_child,reply_num,short_name FROM ask_government where del_flag is null and fup=@fup");
            sql.params().set("fup",0);
            sql.setCallback(Sqls.callback.entities());
            sql.setEntity(dao.getEntity(AskGovernment.class));
            dao.execute(sql);
            List<AskGovernment> listTemp = sql.getList(AskGovernment.class);

            Long fid = listTemp.get(0).getFid();
            sql = Sqls.create("SELECT id,name,area_code,has_child,reply_num,short_name FROM ask_government where del_flag is null and fup =@fup");
            sql.params().set("fup",fid);
            sql.setCallback(Sqls.callback.entities());
            sql.setEntity(dao.getEntity(AskGovernmentType.class));
            dao.execute(sql);

            list = sql.getList(AskGovernmentType.class);
//            cache.put(ALL_GOV_AREA, list);
        }else{
//            if(cache.containsKey(ALL_CITY_AREA)) {
//                return (List<AskGovernmentType>)cache.get(ALL_CITY_AREA);
//            }
            AskGovernment askGovernment = this.fetch(gid);
            Long fid = askGovernment.getFid();
            sql = Sqls.create("SELECT id,name,area_code,has_child,reply_num,short_name FROM ask_government where del_flag is null and fup=@fup");
            sql.params().set("fup",fid);
            sql.setCallback(Sqls.callback.entities());
            sql.setEntity(dao.getEntity(AskGovernmentType.class));
            dao.execute(sql);
            list = sql.getList(AskGovernmentType.class);
//            cache.put(ALL_CITY_AREA, list);
        }
        return list;
    }
}