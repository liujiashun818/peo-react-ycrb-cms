package cn.people.one.modules.user.service.impl;

import cn.people.one.core.aop.annotation.ExCacheEvict;
import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.base.config.RedisConfig;
import cn.people.one.modules.sys.model.Menu;
import cn.people.one.modules.sys.service.IMenuService;
import cn.people.one.modules.user.model.Office;
import cn.people.one.modules.user.model.Role;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IOfficeService;
import cn.people.one.modules.user.service.IRoleService;
import cn.people.one.modules.user.utils.UserUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理下角色管理Service
 *
 * @author cuiyukun
 */
@Service
@Transactional(readOnly = true)
public class RoleService extends BaseService<Role> implements IRoleService {

    private static Long rootId = 1L;
    @Autowired
    private BaseDao dao;

    @Autowired
    private IOfficeService officeService;

    @Autowired
    private IMenuService menuService;

    @Override
    public List<Role> findRoleByUserId(String userId) {
        return null;
    }


    /**
     * 在角色机构关联表中加入新的记录
     *
     * @param role
     */
    @ExCacheEvict(value = {RedisConfig.CATEGORY_TREE},key=RedisConfig.CATEGORY_KEY)
    private void officeRelation(Role role) {
        if (!Lang.isEmpty(role.getDataScope())) {
            List<Office> offices = new ArrayList<>();
            Office office;
            switch (role.getDataScope()) {
                case 1:
                    role.setOffices(officeService.listAll());
                    dao.insertRelation(role, Role.OFFICES);
                    break;
                case 2:
                    office = officeService.fetch(role.getOfficeId());
                    if (office == null) {
                        break;
                    }
                    offices.add(office);
                    role.setOffices(offices);
                    dao.insertRelation(role,  Role.OFFICES);
                    break;
                case 3:
                    office = officeService.fetch(role.getOfficeId());
                    offices.add(office);
                    offices.addAll(dao.query(Office.class, Cnd.where("parent_ids", "LIKE", "%," + role.getOfficeId() + ",%")));
                    role.setOffices(offices);
                    dao.insertRelation(role,  Role.OFFICES);
                    break;
                case 4:
                    //如果角色数据范围是个人数据，直接从个人创建的栏目做控制，不在角色机构关联表中插入数据
                    break;
            }
        }
    }

    /**
     * 根据用户id获取角色信息
     *
     * @param id
     * @return
     */
    @Override
    public Role fetch(Long id) {
        Role role = super.fetch(id);
        if (!Lang.isEmpty(role)) {
            dao.fetchLinks(role,  Role.MENUS);
        }
        List<Long> idList = new ArrayList<>();
        if (!Lang.isEmpty(role.getMenus())) {
            for (Menu menu : role.getMenus()) {
                idList.add(menu.getId());
            }
        }
        role.setMenuIds(idList);
        role.setMenus(null);
        return role;
    }

    /**
     * 增加角
     *
     * @param oriRole
     */
    @Override
    @Transactional
    @ExCacheEvict(value = {RedisConfig.MENU_TREE}, key = RedisConfig.MENU_KEY)
    public Role save(Role oriRole) {
        super.save(oriRole);
        Role role = fetch(oriRole.getName());
        //在角色机构关联表中加入新的记录
        officeRelation(role);
        //在关联表中加入新的记录
        if (!Lang.isEmpty(oriRole.getMenuIds())) {
            List<Long> menuIds =oriRole.getMenuIds();
            role.setMenuIds(menuIds);
            List<Menu> list = new ArrayList<>();
            for (Long id : oriRole.getMenuIds()) {
                Menu menu = menuService.fetch(id);
                list.add(menu);
            }
            role.setMenus(list);
            dao.insertRelation(role,  Role.MENUS);
        }
        return role;
    }

    /**
     * 伪删除角色
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    @ExCacheEvict(value = {RedisConfig.MENU_TREE,RedisConfig.CATEGORY_TREE},
            key=RedisConfig.CATEGORY_KEY+"_"+RedisConfig.MENU_KEY)
    public int vDelete(Long id) {
        return super.vDelete(id);
    }

    @Override
    @Transactional
    @ExCacheEvict(key=RedisConfig.CATEGORY_KEY+"_"+RedisConfig.MENU_KEY)
    public int updateIgnoreNull(Role role) {
        //清除原有的角色机构关联表中的记录
        Role oriRole = dao.fetchLinks(fetch(role.getId()),  Role.OFFICES);
        if (!Lang.isEmpty(oriRole.getOffices())) {
            dao.clearLinks(oriRole,  Role.OFFICES);
        }

        //清除原有的角色菜单关联表中的记录
        oriRole = dao.fetchLinks(fetch(role.getId()),  Role.MENUS);
        if (!Lang.isEmpty(oriRole.getMenus())) {
            dao.clearLinks(oriRole,  Role.MENUS);
        }

        //在角色机构关联表中加入新的记录
        officeRelation(role);

        //在角色菜单关联表中加入新的记录
        if (!Lang.isEmpty(role.getMenuIds())) {
            List<Menu> list = new ArrayList<>();
            for (Long integer : role.getMenuIds()) {
                Menu menu = menuService.fetch(integer);
                list.add(menu);
            }
            role.setMenus(list);
            dao.insertRelation(role,  Role.MENUS);
        }

        int flag = super.updateIgnoreNull(role);
        return flag;
    }

    @Override
    public List<Role> listAll() {
        List<Role> allRoles = query(null, Cnd.where(BaseEntity.FIELD_STATUS, "<", BaseEntity.STATUS_DELETE));
        allRoles.forEach(role -> {
            Office office = officeService.fetch(role.getOfficeId());
            if (Lang.isEmpty(office)) {
                role.setOfficeName(null);
            }else {
                role.setOfficeName(office.getName());
            }
        });
        return allRoles;
    }

    /**
     * 获取角色列表并分页
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public QueryResultVO<Role> listPage(Integer pageNumber, Integer pageSize) {
        return listPage(pageNumber, pageSize, Cnd.where(BaseEntity.FIELD_STATUS, "<", BaseEntity.STATUS_DELETE));
    }

    /**
     * 获取当前用户的角色列表
     *
     * @return
     */
    @Override
    public List<Role> getCurrentUserRole() {
        User user = UserUtils.getUser();
        if (user != null) {
            return user.getRoleList();
        }
        return null;
    }
}