package cn.people.one.modules.sys.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.sys.model.Dict;
import cn.people.one.modules.sys.model.front.DictVO;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
* 字典Service
* @author cuiyukun
*/
public interface IDictService extends IBaseService<Dict>{

    List<Dict> getListByType(String type);
    QueryResultVO<Dict> listPage(Integer pageNo, Integer pageSize, DictVO dictVO);

    List getTypes();

    /**
     * 获取指定类型、指定值的一个字典
     * @param type
     * @param value
     * @return 查不到时为null
     */
    Dict fetchByTypeValue(String type,String value);
}