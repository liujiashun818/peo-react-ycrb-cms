package cn.people.one.modules.user.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.user.model.Role;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
* 用户管理下角色管理Service
* @author cuiyukun
*/
public interface IRoleService extends IBaseService<Role>{

    List<Role> findRoleByUserId(String userId);

    List<Role> getCurrentUserRole();

    List<Role> listAll();

    QueryResultVO<Role> listPage(Integer pageNumber, Integer pageSize);

}