package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.aop.annotation.ExCacheEvict;
import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.modules.base.config.RedisConfig;
import cn.people.one.modules.base.service.impl.TreeService;
import cn.people.one.modules.client.model.ClientMenu;
import cn.people.one.modules.client.service.IClientMenuService;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.model.CategoryMeta;
import cn.people.one.modules.cms.model.FieldGroup;
import cn.people.one.modules.cms.model.front.CategoryVO;
import cn.people.one.modules.cms.service.ICategoryService;
import cn.people.one.modules.cms.service.IFieldGroupService;
import cn.people.one.modules.user.model.Office;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IOfficeService;
import cn.people.one.modules.user.service.IUserService;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.sql.Criteria;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by lml on 2016/12/22.
 */
@Service
@Transactional(readOnly = true)
public class CategoryService extends TreeService<Category> implements ICategoryService {

    @Autowired
    private BaseDao dao;

    @Autowired
    private IOfficeService officeService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IFieldGroupService fieldGroupService;

    @Autowired
    IClientMenuService clientMenuService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @ExCacheEvict(value = RedisConfig.CATEGORY_TREE)
    @Transactional
    public Category save(Category category) {
        if (category.getId() == null) {
            List<CategoryMeta> metaList = category.getMetas();
            //插入扩展字段
            if (!Lang.isEmpty(metaList)) {
                dao.insertLinks(category, Category.MATAS);
            }
        } else {
            dao.clearLinks(category, Category.MATAS);
            dao.insertLinks(category, Category.MATAS);
        }
        super.save(category);
        return category;
    }

    @Override
    @Transactional
    @ExCacheEvict(value = RedisConfig.CATEGORY_TREE)
    public int updateIgnoreNull(Category category) {
        return super.updateIgnoreNull(category);
    }

    @Override
    //字段组 含字段信息
    public List<FieldGroup> getOwnerFieldGroups(Long categoryId) {
        Category category = dao.fetchLinks(fetch(categoryId), Category.FIELD_GROUPS);
        if (category != null) {
            List<FieldGroup> groups = category.getFieldGroups();
            return fieldGroupService.setInfo(groups);
        }
        return null;
    }

    @ExCacheEvict(value = RedisConfig.CATEGORY_TREE)
    @Transactional
    public int delete(Long id) {
        return this.vDelete(id);
    }

    @Override
//    @Cacheable(value = RedisConfig.CATEGORY_TREE)
    public List<CategoryVO> getTree(Long id, Long userId) {
        return getCurrentUserCategory(id, userId, null);
    }

    public List<CategoryVO> getCurrentUserCategory(Long id, Long userId, Integer delFlag) {
        User user = userService.fetch(userId);
        if (user == null) {
            return null;
        }
        Set<Long> categoryIds = userService.getCategoryIdsInOffices(user);
        List<Category> sourceList = queryByParentId(id, delFlag);
        List<CategoryVO> list = BeanMapper.mapList(sourceList, CategoryVO.class);
        list = setChild(list, categoryIds);
        return list;
    }

    private CategoryVO recursive(CategoryVO categoryVO, Set categoryIds) {
        List<Category> categories = queryByParentId(categoryVO.getId(), null);
        List<CategoryVO> child = BeanMapper.mapList(categories, CategoryVO.class);
        child = setChild(child, categoryIds);
        categoryVO.setChild(child);
        return categoryVO;
    }

    private List<CategoryVO> setChild(List<CategoryVO> list, Set categoryIds) {
        if ((Lang.isEmpty(categoryIds)) || Lang.isEmpty(list)) {
            return null;
        }
        Iterator<CategoryVO> iterator = list.iterator();
        while (iterator.hasNext()) {
            CategoryVO categoryVO = iterator.next();
            if (categoryVO == null || !categoryIds.contains(categoryVO.getId())) {
                iterator.remove();
                continue;
            }
            if (categoryVO.getOfficeId() != null) {
                Office office = officeService.fetch(categoryVO.getOfficeId());
                if (office != null) {
                    categoryVO.setOfficeName(office.getName());
                }
            }
            List<ClientMenu> clientMenus=dao.query(ClientMenu.class,Cnd.where("category_id","=",categoryVO.getId()).and(ClientMenu.FIELD_STATUS,"=",ClientMenu.STATUS_ONLINE));
            categoryVO.setMenuList(clientMenus);
            recursive(categoryVO, categoryIds);
        }
        return list;
    }

    @Override
    public QueryResultVO<Category> listPage(Integer pageNo, Integer pageSize) {
        QueryResultVO<Category> result = super.listPage(pageNo, pageSize, getDelFlag(null));
        return result;
    }

    /**
     * 获取栏目详情 包含栏目本身扩展的字段
     *
     * @param id
     * @return
     */
    @Override
    public Category fetch(Long id) {
        Category category = super.fetch(id);
        //查询关联字段
        category = dao.fetchLinks(dao.fetchLinks(category, Category.FIELD_GROUPS), Category.MATAS);
        if (category != null && !Lang.isEmpty(category.getFieldGroups())) {
            category.setFieldGroups(fieldGroupService.setInfo(category.getFieldGroups()));
        }
        return category;
    }


    /**
     * 不含关联字段
     *
     * @param list
     */
    @Override
    @Transactional
    @ExCacheEvict(value = RedisConfig.CATEGORY_TREE)
    public void batchUpdate(List<Category> list) {
        super.batchUpdate(list);
    }

    @Override
    public List<Category> getAllChildrenList(Set<Long> ids, List<Category> list, Long categoryId) {
        List<Category> child = queryByParentId(categoryId, null);
        if (Lang.isEmpty(child)) {
            return list;
        }
        Iterator<Category> iterator = child.iterator();
        while (iterator.hasNext()) {
            Category category = iterator.next();
            if (category == null) {
                iterator.remove();
                continue;
            }
            if (!ids.contains(category.getId())) {
                iterator.remove();
                continue;
            }
            list.add(category);
            list = getAllChildrenList(ids, list, category.getId());
        }
        return list;
    }

    /**
     * 根据父栏目ID查询子栏目
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Category> queryByParentId(Long categoryId) {
        List<Category> child = queryByParentId(categoryId, null);
        return child;
    }

    /**
     * 判断是否重名
     * @param name
     * @return
     */
    @Override
    public Boolean isRepeatName(String name) {
        List<Category> categoryList=dao.query(Category.class, Cnd.where(Category.FIELD_STATUS,"=",Category.STATUS_ONLINE).and(Category.Constant.NAME,"=",name));
        if(categoryList!=null && categoryList.size()>0){
            return true;
        }
        return false;
    }

    @Override
    public Category getCategoryByName(Category category) {
        return dao.fetch(Category.class,Cnd.where("name","=",category.getName()).and("parent_id","=",category.getParentId()));
    }

    /**
     * 查询该机构是否关联了客户端菜单
     * @param id
     * @return
     */
    @Override
    public String isRelatedToCategory(Long id){
        Criteria cri = Cnd.cri();
        cri.where().and("category_id","=",id);
        cri.where().and("del_flag", "<", 3);
        QueryResult queryResult=clientMenuService.listPage(1,1,cri);
        if(queryResult.getList().size()>0){
            return "该栏目已经关联了客户端菜单，请解除联系后删除";
        }
        return "false";
    }
}