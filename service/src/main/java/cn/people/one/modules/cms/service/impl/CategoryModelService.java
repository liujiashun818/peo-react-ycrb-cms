package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.CategoryModel;
import cn.people.one.modules.cms.service.ICategoryModelService;
import cn.people.one.modules.cms.service.IFieldGroupService;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lml on 2017/4/28.
 */
@Service
@Transactional(readOnly = true)
public class CategoryModelService extends BaseService<CategoryModel> implements ICategoryModelService {

    @Autowired
    private BaseDao dao;

    @Autowired
    private IFieldGroupService fieldGroupService;

    @Override
    public List<CategoryModel> getAll(){
        return dao.query(CategoryModel.class, Cnd.where(BaseEntity.FIELD_STATUS,"<",BaseEntity.STATUS_DELETE));
    }

    /**
     * 获取栏目模型详情  模型下的文章扩展字段的信息
     * @param id
     * @return
     */
    @Override
    public CategoryModel fetch(Long id) {
        CategoryModel categoryModel = super.fetch(id);
        //查询关联字段
        categoryModel = dao.fetchLinks(categoryModel, CategoryModel.FIELD_GROUPS);
        if(!Lang.isEmpty(categoryModel.getFieldGroups())){
            categoryModel.setFieldGroups(fieldGroupService.setInfo(categoryModel.getFieldGroups()));
        }
        return categoryModel;
    }

    @Override
    public QueryResultVO<CategoryModel> listPage(Integer pageNo, Integer pageSize) {
        QueryResultVO<CategoryModel> result = super.listPage(pageNo, pageSize, getDelFlag(null));
        return result;
    }

}
