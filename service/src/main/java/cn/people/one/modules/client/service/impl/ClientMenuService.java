package cn.people.one.modules.client.service.impl;

import cn.people.one.core.aop.annotation.ExCacheEvict;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.modules.base.config.RedisConfig;
import cn.people.one.modules.base.service.impl.TreeService;
import cn.people.one.modules.client.model.ClientMenu;
import cn.people.one.modules.client.model.front.ClientMenuVO;
import cn.people.one.modules.client.service.IClientMenuService;
import cn.people.one.modules.cms.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lml on 17-2-14.
 */
@Service
@Transactional(readOnly = true)
public class ClientMenuService extends TreeService<ClientMenu> implements IClientMenuService {

    @Autowired
    private BaseDao dao;

    public List<ClientMenu> getMenuList() {
        return dao.query(tClass, getDelFlag(BaseEntity.STATUS_ONLINE).desc("sort").asc("id"));
    }

    @Override
    @ExCacheEvict(value = {RedisConfig.CLIENT_TREE})
    @Transactional
    public ClientMenu save(ClientMenu clientMenu) {
        return super.save(clientMenu);
    }


    @ExCacheEvict(value = RedisConfig.CLIENT_TREE)
    @Transactional
    public int delete(Long id) {
        return this.vDelete(id);
    }

    @Override
    @Cacheable(value = RedisConfig.CLIENT_TREE)
    public List<ClientMenuVO> getTree(Long id) {
        return getMenuTree(id, null);
    }

    @Override
    @Cacheable(value = RedisConfig.CLIENT_TREE)
    public List<ClientMenuVO> getTreeView(Long id, Integer delFlag) {
        return getMenuTree(id, delFlag);
    }

    public List<ClientMenuVO> getMenuTree(Long id, Integer delFlag) {
        List sourceList = queryByParentId(id, delFlag);
        List<ClientMenuVO> list = BeanMapper.mapList(sourceList, ClientMenuVO.class);
        if (null != list && list.size() > 0) {
            list.stream().forEach(menu -> recursive(menu, delFlag));
        } else {
            return null;
        }
        return list;
    }

    /**
     * 保存客户端菜单
     * @param clientMenu
     */
    @Override
    @Transactional
    @ExCacheEvict(value = RedisConfig.CLIENT_TREE)
    public void saveClientMenu(ClientMenu clientMenu) {
        if(clientMenu.getId()==null){
            super.save(clientMenu);
        }else {
            if(clientMenu.getCategoryId()==null){
                //栏目id允许至成空
                dao.update(clientMenu);
            }else{
                dao.updateIgnoreNull(clientMenu);
            }
        }
    }

    private ClientMenuVO recursive(ClientMenuVO menuVO, Integer delFlag) {
        List<ClientMenu> menus = queryByParentId(menuVO.getId(), delFlag);
        List<ClientMenuVO> child = BeanMapper.mapList(menus, ClientMenuVO.class);
        if (null != child && child.size() > 0) {
            child.stream().forEach(menu -> {
                if (menu.getCategoryId() != null) {
                    Category category = dao.fetch(Category.class, menu.getCategoryId());
                    if (category != null) {
                        menu.setCategoryName(category.getName());
                    }
                }
                recursive(menu, delFlag);
            });
        }
        menuVO.setChild(child);
        return menuVO;
    }

    @Override
    @Transactional
    @ExCacheEvict(value = RedisConfig.CLIENT_TREE)
    public int updateIgnoreNull(ClientMenu clientMenu) {
        clientMenu.setDelFlag(BaseEntity.STATUS_ONLINE);
        return super.updateIgnoreNull(clientMenu);
    }

	@Override
	public List<ClientMenu> queryMenuByParentId(Long parentId, Integer delFlag, Integer tabType) {
		return dao.query(tClass, getDelFlag(delFlag).and("parent_id", "=", parentId).and("tabType","=",tabType).desc("sort").desc("create_at"));
	}

	
	

}
