package cn.people.one.modules.cms.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.cms.model.CategoryModel;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
 * Created by lml on 2017/4/28.
 */
public interface ICategoryModelService  extends IBaseService<CategoryModel>{

    List<CategoryModel> getAll();

    QueryResultVO<CategoryModel> listPage(Integer pageNo, Integer pageSize);
}
