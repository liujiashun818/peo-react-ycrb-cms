package cn.people.one.modules.cms.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.cms.model.Field;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
* 字段描述Service
* @author lml
*/
public interface IFieldService extends IBaseService<Field>{

    List<Field> findFieldsByGroupId(Long groupId);

    void addFields(Long groupId, List<Field> fields);

    QueryResult listPage(Integer pageNo, Integer pageSize);

    Boolean slugExist(String slug);
}