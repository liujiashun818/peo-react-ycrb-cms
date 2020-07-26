package cn.people.one.modules.user.service.impl;

import cn.people.one.core.aop.annotation.ExCacheEvict;
import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.base.config.RedisConfig;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.sys.model.Menu;
import cn.people.one.modules.user.model.Office;
import cn.people.one.modules.user.model.Role;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IOfficeService;
import cn.people.one.modules.user.service.IRoleService;
import cn.people.one.modules.user.service.IUserService;
import cn.people.one.modules.user.utils.UserUtils;
import com.google.common.collect.Lists;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户Service,用户增删改查操作
 * 获取用户角色、菜单、机构等信息
 * @author cuiyukun
 */
@Service
@Transactional(readOnly = true)
public class UserService extends BaseService<User> implements IUserService {

    @Autowired
    private BaseDao dao;

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IOfficeService officeService;
    /**
     * username  管理员
     * 根据username获取用户信息
     *
     * @param username
     * @return
     */
    @Override
    public User fetch(String username) {
        User user = super.fetch(username);
//        if (user != null) {
//            dao.fetchLinks(user, User.ROLE_LIST);
//        }
        return user;
    }

    /**
     * 根据用户id获取用户信息
     * 将用户具有的权限id放入user
     * @param id
     * @return
     */
    @Override
    public User fetch(Long id){
        User user = super.fetch(id);
        if(user==null){
            return user;
        }
        dao.fetchLinks(user, User.ROLE_LIST);
        List<Long> idList = new ArrayList<>();
        if (!Lang.isEmpty(user.getRoleList())){
            for (Role role : user.getRoleList()) {
                idList.add(role.getId());
            }
        }
        user.setRoleIds(idList);
        user.setRoleList(null);
        user.setPassword(null);
        return user;
    }

    /**
     * 伪删除用户
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

    /**
     * 增加用户
     *
     * @param oriUser
     */
    @Override
    @Transactional
    @ExCacheEvict(value = {RedisConfig.MENU_TREE,RedisConfig.CATEGORY_TREE},
            key=RedisConfig.CATEGORY_KEY+"_"+RedisConfig.MENU_KEY)
    public User save(User oriUser) {
        if (Lang.isEmpty(oriUser.getPassword())) {
            return null;
        }
        oriUser.setPassword(UserUtils.encodePassword(oriUser.getPassword()));
        super.save(oriUser);
        User user = fetch(oriUser.getUsername());
        //在关联表中加入新的记录
        if (!Lang.isEmpty(oriUser.getRoleIds())) {
            List<Role> list = new ArrayList<>();
            for (Long id : oriUser.getRoleIds()) {
                Role role = roleService.fetch(id);
                list.add(role);
            }
            user.setRoleList(list);
            dao.insertRelation(user, User.ROLE_LIST);
        }
        return user;
    }

    /**
     * 更新用户
     *
     * @param user
     */
    @Override
    @Transactional
    @ExCacheEvict(value = {RedisConfig.MENU_TREE,RedisConfig.CATEGORY_TREE},
            key=RedisConfig.CATEGORY_KEY+"_"+RedisConfig.MENU_KEY)
    public int update(User user, String fieldName) {
        return super.update(user, fieldName);
    }

    @Transactional
    @ExCacheEvict(value = {RedisConfig.MENU_TREE,RedisConfig.CATEGORY_TREE},
            key=RedisConfig.CATEGORY_KEY+"_"+RedisConfig.MENU_KEY)
    public int updateUser(User user) {
        //清除原有的用户角色关联表中的记录git
        User oriUser = dao.fetchLinks(fetch(user.getId()), User.ROLE_LIST);
        if (!Lang.isEmpty(oriUser.getRoleList())) {
            dao.clearLinks(oriUser, User.ROLE_LIST);
        }

        //在关联表中加入新的记录
        if (!Lang.isEmpty(user.getRoleIds())) {
            List<Role> list = new ArrayList<>();
            for (Long id : user.getRoleIds()) {
                Role role = roleService.fetch(id);
                list.add(role);
            }
            user.setRoleList(list);
            dao.insertRelation(user, User.ROLE_LIST);
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(null);
        } else {
            user.setPassword(UserUtils.encodePassword(user.getPassword()));
        }
        int flag = super.updateIgnoreNull(user);
        return flag;
    }

    /**
     * 获取用户列表并分页
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public QueryResultVO<User> listPage(Integer pageNumber, Integer pageSize) {
        return super.listPage(pageNumber, pageSize, Cnd.where(BaseEntity.FIELD_STATUS,"<",BaseEntity.STATUS_DELETE), "^id|username|name|remark$");
    }

    public Set<Integer> getMenusSet(User user){
        Set set = new HashSet();
        if(user.isAdmin()){
            List<Menu>list = dao.query(Menu.class,Cnd.where(BaseEntity.FIELD_STATUS,"<",Menu.STATUS_DELETE));
            list.forEach(menu -> set.add(menu.getId()));
            return set;
        }
        List<Role> roles = getRoles(user);
        if(!Lang.isEmpty(roles)){
            roles.stream().forEach(role -> {
                if(role!=null){
                    Role _role = dao.fetchLinks(role, Role.MENUS);
                    if(!Lang.isEmpty(_role.getMenus())){
                        _role.getMenus().forEach(menu -> {
                            if(StringUtils.isBlank(menu.getParentIds())){
                                menu.setParentIds("0,1,");
                            }
                            Set<Long> parents = Stream.of(menu.getParentIds().split(",")).map(parentId -> {
                                if (parentId != null) {
                                    return Long.valueOf(parentId);
                                }
                                return null;
                            }).collect(Collectors.toSet());
                            if (!Lang.isEmpty(parents)) {
                                set.addAll(parents);
                            }
                            set.add(menu.getId());
                        });
                    }
                }
            });
        }
        return set;
    }

    public List<Role> getRoles(User user){
        if(user!=null){
            user = dao.fetchLinks(user, User.ROLE_LIST);
            List<Role> roles = user.getRoleList();
            return roles;
        }
        return null;
    }

    @Override
    public List<Menu> getUserMenus(User user) {
        List<Role> roles = getRoles(user);
        List<Menu> menus = Lists.newArrayList();
        if(!Lang.isEmpty(roles)){
            for (Role role : roles) {
                role = dao.fetchLinks(role, Role.MENUS);
                menus.addAll(role.getMenus());
            }
        }
        return menus;
    }

    public List<Category> getCreateCategorys(User user){
        List<Role> roles = getRoles(user);
        if(user!=null && !Lang.isEmpty(roles)){
            for(Role role:roles){
                if(role.getDataScope().equals(Role.DATA_SCOPE_OWNER)){
                    List<Category>categories = dao.query(Category.class,Cnd.where(BaseEntity.FIELD_STATUS,"<",Category.STATUS_DELETE).and("create_by","=",user.getId()));
                    return categories;
                }
            }
        }
        return null;
    }

    public Set<Long> getCategoryIdsInOffices(User user){
        Set<Long> categoryIds = new HashSet();
        Set<Long> officeIds = new HashSet<>();
        List<Category> categoryList = new ArrayList<>();
        if(user.isAdmin()){
            List<Category> categorys = dao.query(Category.class,Cnd.where(BaseEntity.FIELD_STATUS,"<",Category.STATUS_DELETE));
            if(!Lang.isEmpty(categorys)){
                categorys.stream().forEach(category->{
                    Office office = officeService.fetch(category.getOfficeId());
                    if (office != null) {
                        categoryIds.add(category.getId());
                    }
                });
            }
            return categoryIds;
        }
        List<Role>roles = getRoles(user);
        if(!Lang.isEmpty(roles)){
            roles.stream().forEach(role -> {
                role = dao.fetchLinks(role,Role.OFFICES);
                if(!Lang.isEmpty(role.getOffices())){
                    officeIds.addAll(role.getOffices().stream().map(office -> office.getId()).collect(Collectors.toSet()));
                }
            });
        }
        if(!Lang.isEmpty(officeIds)){
            String[] ids = new String[officeIds.size()];
            if(!Lang.isEmpty(officeIds)){
                int i = 0;
                for(Long id:officeIds){
                    ids[i++] = id.toString();
                }
            }
            Criteria cnd = Cnd.NEW().getCri();
            cnd.where().and(BaseEntity.FIELD_STATUS,"<",Category.STATUS_DELETE).andIn("office_id",ids);
            categoryList = dao.query(Category.class,cnd);
        }

        //如果有个人创建的菜单则合并
        List<Category> ownerCategorys = getCreateCategorys(user);
        if(!Lang.isEmpty(ownerCategorys)){
            for(Category category:ownerCategorys){
                if(!categoryList.contains(category)){
                    categoryList.add(category);
                }
            }
        }
        if(!Lang.isEmpty(categoryList)){
            categoryList.forEach(category -> {
                //将每个栏目和对应的父级栏目找到,加入列表中
                categoryIds.add(category.getId());
                Set<Long> parents = Stream.of(category.getParentIds().split(",")).map(parentId -> {
                    if (parentId != null) {
                        return Long.valueOf(parentId);
                    }
                    return null;
                }).collect(Collectors.toSet());
                categoryIds.addAll(parents);
            });
        }
        return categoryIds;
    }

    @Override
    public User getUserByUpmsUserId(String upmsUserId) {
        return dao.fetch(User.class,Cnd.where(User.Constant.UPMSUSERID,"=",upmsUserId).and(User.FIELD_STATUS,"=",User.STATUS_ONLINE));
    }

    @Override
    public int deleteByUpmsUserId(String upmsUserId) {
        return dao.update(User.class, Chain.make(User.FIELD_STATUS,User.STATUS_DELETE),Cnd.where(User.Constant.UPMSUSERID,"=",upmsUserId));
    }

    /**
     * 根据账号查询用户，区分大小写
     * @param userName
     * @return
     */
    @Override
    public User fetchByBINARY(String userName) {
        Sql sql=Sqls.create("SELECT * FROM sys_user WHERE BINARY username=@userName");
        sql.setParam("userName",userName);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(User.class));
        dao.execute(sql);
        User user= sql.getObject(User.class);
        return user;
    }

    @Override
    public List<User>findAll(){
        return dao.query(User.class,Cnd.where(User.FIELD_STATUS,"<",User.STATUS_DELETE));
    }


}