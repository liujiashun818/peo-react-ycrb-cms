package cn.people.one.modules.cms.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.cms.model.FieldGroup;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
* 字段组Service
* @author lml
*/
public interface IFieldGroupService extends IBaseService<FieldGroup>{

    List<FieldGroup> getListByName(String name);

    List<FieldGroup> setInfo(List<FieldGroup> groups);

    QueryResult listPage(Integer pageNo, Integer pageSize);
}