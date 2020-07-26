package cn.people.one.modules.cms.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.ITreeService;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.model.FieldGroup;
import cn.people.one.modules.cms.model.front.CategoryVO;
import org.nutz.dao.QueryResult;

import java.util.List;
import java.util.Set;

/**
 * Created by lml on 2016/12/22.
 */
public interface ICategoryService extends ITreeService<Category> {

    List<CategoryVO> getTree(Long id,Long userId);
    List<FieldGroup>getOwnerFieldGroups(Long categoryId);
    int updateIgnoreNull(Category category);
    void batchUpdate(List<Category> list);
    QueryResultVO<Category> listPage(Integer pageNo, Integer pageSize);
    List<Category> getAllChildrenList(Set<Long> ids,List<Category> list, Long categoryId);
    List<Category> queryByParentId(Long categoryId);

    /**
     * 判断是否重名
     * @param name
     */
    Boolean isRepeatName(String name);

    Category getCategoryByName(Category category);

    String isRelatedToCategory(Long id);
}
