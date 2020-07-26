package cn.people.one.modules.sys.service.impl;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.sys.model.Dict;
import cn.people.one.modules.sys.model.front.DictVO;
import cn.people.one.modules.sys.service.IDictService;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* 字典Service
* @author cuiyukun
*/
@Service
@Transactional(readOnly = true)
public class DictService extends BaseService<Dict> implements IDictService {

    @Autowired
    private BaseDao dao;

    /**
     * 添加一条字典记录
     *
     * @param dict
     */
    @Override
    @Transactional(readOnly = false)
    public Object save(Dict dict)  {
        return super.save(dict);
    }

    /**
     * 伪删除一条字典
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public int vDelete(Long id) {
        return super.vDelete(id);
    }

    @Override
    public List<Dict> getListByType(String type) {
        return dao.query(tClass,getDelFlag(null).and(Dict.Constant.TYPE,"=",type));
    }

    @Override
    public Dict fetchByTypeValue(String type,String value) {
        List<Dict> ls = dao.query(tClass,getDelFlag(null).and(Dict.Constant.TYPE,"=",type).and("value","=",value));
        if(ls!=null && !ls.isEmpty()) {
            return ls.get(0);  //只返回第一个
        }

        return null;
    }

    @Override
    public QueryResultVO<Dict> listPage(Integer pageNo, Integer pageSize, DictVO dictVO){
        Cnd cnd = Cnd.NEW();
        if(StringUtils.isNotBlank(dictVO.getDescription())){
            cnd.and(Dict.Constant.DESCRIPTION,"like","%"+dictVO.getDescription().trim()+"%");
        }
        if(StringUtils.isNotBlank(dictVO.getType())){
            cnd.and(Dict.Constant.TYPE,"=",dictVO.getType());
        }
        cnd.and(BaseEntity.FIELD_STATUS, "<", BaseEntity.STATUS_DELETE).desc(Dict.Constant.ID);
        return listPage(pageNo,pageSize,cnd);
    }

    @Override
    public List getTypes() {
        Sql sql = Sqls.create("select distinct type from "+dao.getEntity(Dict.class).getTableName() +
                " where del_flag <3");
        return list(sql);
    }
}