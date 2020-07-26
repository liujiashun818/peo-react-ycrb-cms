package cn.people.one.modules.sys.service.impl;

import cn.people.one.core.aop.annotation.ExCacheEvict;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.modules.base.config.RedisConfig;
import cn.people.one.modules.base.service.impl.TreeService;
import cn.people.one.modules.sys.model.Menu;
import cn.people.one.modules.sys.model.front.NavMenuVO;
import cn.people.one.modules.sys.service.IMenuService;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 用户管理下菜单管理Service
 *
 * @author cuiyukun
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class MenuService extends TreeService<Menu> implements IMenuService {

    @Autowired
    private BaseDao dao;

    @Autowired
    private IUserService userService;

    @Override
    public Menu queryCode(String code) {
        //过滤已经删除的数据
        List<Menu> result = dao.query(tClass, getDelFlag(null).and("code", "=", code));
        return result.size() > 0 ? result.get(0) : null;
    }

    /**
     * 增加一条菜单
     *
     * @param menu
     */
    @Override
    @ExCacheEvict(value = RedisConfig.MENU_TREE)
    @Transactional
    public Menu save(Menu menu) {
        return super.save(menu);
    }

    /**
     * 伪删除菜单
     *
     * @param id
     * @return
     */
    @Override
    @ExCacheEvict(value = RedisConfig.MENU_TREE)
    @Transactional
    public int vDelete(Long id) {
        return super.vDelete(id);
    }

    /**
     * 更新一条菜单信息
     *
     * @param menu
     */
    @Override
    @ExCacheEvict(value = RedisConfig.MENU_TREE)
    @Transactional
    public int update(Menu menu, String fieldName) {
        return super.update(menu, fieldName);
    }

    @Override
    @Cacheable(value = RedisConfig.MENU_TREE)
    public List<NavMenuVO> getMenuTree(Long id,Long userId) {
        return getCurrentUserMenu(id, userId,null);
    }

    @Override
    @Cacheable(value = RedisConfig.MENU_TREE)
    public List<NavMenuVO> getMenuTree(Long id, Boolean filterView,Long userId) {
        return getCurrentUserMenu(id,userId, filterView);
    }

    @Override
    @Transactional
    @ExCacheEvict(value = RedisConfig.MENU_TREE)
    public int updateIgnoreNull(Menu menu) {
        return super.updateIgnoreNull(menu);
    }

    @Override
    @Transactional
    @ExCacheEvict(value = RedisConfig.MENU_TREE)
    public void batchUpdate(List<Menu> list) {
        super.batchUpdate(list);
    }

    /**
     * 返回菜单树
     *
     * @return
     */
    @Cacheable(value = RedisConfig.MENU_TREE)
    public List<NavMenuVO> getCurrentUserMenu(Long id, Long userId, Boolean filterView) {
        User user = userService.fetch(userId);
        Set menuIds = userService.getMenusSet(user);
        if(Lang.isEmpty(menuIds)){
            return null;
        }
        List<Menu> sourceList = queryByParentId(filterView, id);
        List<NavMenuVO> list = BeanMapper.mapList(sourceList, NavMenuVO.class);
        list = setChild(list,menuIds,filterView);
        return list;
    }

    private NavMenuVO recursive(NavMenuVO menuVO, Boolean filterView, Set menuList) {
        List<Menu> menus = queryByParentId(filterView, menuVO.getId());
        List<NavMenuVO> child = BeanMapper.mapList(menus, NavMenuVO.class);
        child = setChild(child,menuList,filterView);
        menuVO.setChild(child);
        return menuVO;
    }

    private List<NavMenuVO> setChild(List<NavMenuVO> list,Set menuList, Boolean filterView){
        if(!Lang.isEmpty(menuList)){
            if (null != list && list.size() > 0) {
                Iterator<NavMenuVO> iterator = list.iterator();
                while (iterator.hasNext()){
                    NavMenuVO menu = iterator.next();
                    if(menu!=null){
                        if (! menuList.contains(menu.getId())) {
                            iterator.remove();
                            if(Lang.isEmpty(list)){
                                return null;
                            }
                        } else {
                            menu.setKey(menu.getCode());
                            recursive(menu, filterView,menuList);
                        }
                    }
                }
            }
        }else {
            return null;
        }
        return list;
    }


    /**
     * 菜单管理--批量更新排序
     * @param ids
     * @param sorts
     * @return
     */
    @Override
    @Transactional(readOnly = false)
    public int updateSort(Long[] ids , Integer[] sorts) {
        int len = ids.length;
        Menu[] menus = new Menu[len];
        try {
            for (int i = 0; i < len; i++) {
                menus[i] = this.fetch(ids[i]);
                menus[i].setSort(sorts[i]);
                this.updateIgnoreNull(menus[i]);
            }
        }catch (Exception e){
            log.error("批量更新排序失败",e);
            return 0;
        }
        return 1;
    }

    @Override
    @ExCacheEvict(value = RedisConfig.MENU_TREE)
    @Transactional
    public void upadte(Menu menu) {
        dao.update(menu);
    }
}