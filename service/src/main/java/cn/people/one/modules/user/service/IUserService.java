package cn.people.one.modules.user.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.sys.model.Menu;
import cn.people.one.modules.user.model.Role;
import cn.people.one.modules.user.model.User;
import org.nutz.dao.QueryResult;

import java.util.List;
import java.util.Set;

/**
* 用户Service
* @author cuiyukun
*/
public interface IUserService extends IBaseService<User>{
        QueryResultVO<User> listPage(Integer pageNumber, Integer pageSize);

        List<Menu> getUserMenus(User user);

        int updateUser(User user);

        Set<Integer> getMenusSet(User user);

        List<Role> getRoles(User user);

        List<User>findAll();
        Set<Long> getCategoryIdsInOffices(User user);

        /**
         * 根据用户中心用户id查询用户对象
         * @param upmsUserId
         * @return
         */
        User getUserByUpmsUserId(String upmsUserId);

        /**
         * 根据upms用户id删除用户(逻辑)
         * @param upmsUserId
         * @return
         */
        int deleteByUpmsUserId(String upmsUserId);

        /**
         * 根据账号查询用户(区分大小写)
         * @param userName
         * @return
         */
        User fetchByBINARY(String userName);
}