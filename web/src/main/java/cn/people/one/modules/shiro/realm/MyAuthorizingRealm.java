package cn.people.one.modules.shiro.realm;

import cn.people.one.core.util.text.EncodeUtil;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.shiro.captcha.CaptchaException;
import cn.people.one.modules.shiro.loginLock.LoginLockException;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import cn.people.one.modules.user.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 *  @author cuiyukun
 */
@Component
@Slf4j
public class MyAuthorizingRealm extends AuthorizingRealm{

    @Autowired
    private IUserService userService;

    /**
     * 认证信息.(身份验证)
     * :
     * Authentication 是用来验证用户身份
     *
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {

        log.info("MyShiroRealm.doGetAuthenticationInfo()");
        MyUsernamePasswordToken token= (MyUsernamePasswordToken) authcToken;
        //TODO 判断是否已经登录失败超限
        /*String forbidUser=UserUtils.getLoginAccount(token.getUsername());
        if(StringUtils.isNotBlank(forbidUser)){
            throw new LoginLockException();
        }*/
        /**
         * 校验登录验证码
         */
        if(!(token.isRememberMe() && User.REMEMBERME_VALIDATECODE.equals(token.getCaptcha()))){
            Session session=SecurityUtils.getSubject().getSession();
            String validateCode = (String)session.getAttribute("validateCode");
            if(StringUtils.isBlank(validateCode)){
                throw new CaptchaException("验证码已失效!");
            }
            if (token.getCaptcha() == null || StringUtils.isBlank(validateCode)|| !token.getCaptcha().toUpperCase().equals(validateCode.toUpperCase())){
                throw new CaptchaException("验证码错误, 请重试!");
            }
            log.info("captcha:"+token.getCaptcha());
            log.info("validateCode:"+validateCode);
        }
        //获取用户的输入的账号.
        //通过username从数据库中查找User对象
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        String username = token.getUsername();
        User user = userService.fetchByBINARY(username);
        if (user == null){
            return null;
        }
        byte[] salt = EncodeUtil.decodeHex(user.getPassword().substring(0,16));
        //账号判断;
        //加密方式;
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，        如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
            user, //用户名
            user.getPassword().substring(16), //密码
            ByteSource.Util.bytes(salt), //盐
            getName()  //real name
        );
        return authenticationInfo;
    }


    /**
     * 此方法调用  hasRole,hasPermission的时候才会进行回调.
     * <p/>
     * 权限信息.(授权):
     * 1、如果用户正常退出，缓存自动清空；
     * 2、如果用户非正常退出，缓存自动清空；
     * 3、如果我们修改了用户的权限，而用户不退出系统，修改的权限无法立即生效。
     * （需要手动编程进行实现；放在service进行调用）
     * 在权限修改后调用realm中的方法，realm已经由spring管理，所以从spring中获取realm实例，
     * 调用clearCached方法；
     * :Authorization 是授权访问控制，用于对用户进行的操作授权，证明该用户是否允许进行当前操作，如访问某个链接，某个资源文件等。
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
       /*
        * 当没有使用缓存的时候，不断刷新页面的话，这个代码会不断执行，
        * 但其实没有必要每次都重新设置权限信息，所以我们需要放到缓存中进行管理；
        * 当放到缓存中时，这样的话，doGetAuthorizationInfo就只会执行一次了，
        * 缓存过期之后会再次执行。
        */
       log.info("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");

        User loginUser = (User) principals.getPrimaryPrincipal();
        String username = loginUser.getUsername();

//        String username = (String) principals.getPrimaryPrincipal();
        User user = userService.fetch(username);
        if (user == null) {
            return null;
        }
        //权限信息对象authorizationInfo，用来存放查出的用户所有的角色role和权限permission
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //权限单个添加;
        //或者按下面这样添加
        //添加一个角色,不是配置意义上的添加,而是证明该用户拥有admin角色
        //authorizationInfo.addRole("admin");
        //添加权限
        //authorizationInfo.addStringPermission("userInfo:query");

        UserUtils.getCurrentPermission(user);
        authorizationInfo.addStringPermissions(user.getPermissions());

        ///在认证成功之后返回.
        //设置角色信息.
        //支持 Set集合,
        //用户的角色对应的所有权限，如果只使用角色定义访问权限，下面的四行可以不要
//        List<Role> roleList=user.getRoleList();
//        for (Role role : roleList) {
//            info.addStringPermissions(role.getPermissionsName());
//        }
//        for (Role role : user.getRoleList()) {
//            authorizationInfo.addRole(role.getRole());
//            for (Menu menu : role.getMenus()) {
                // authorizationInfo.addStringPermission(menu.getPermission());
//            }
//        }
        //设置权限信息.
//     authorizationInfo.setStringPermissions(getStringPermissions(userInfo.getRoleList()));
        return authorizationInfo;
    }

    @Override
    protected void checkPermission(Permission permission, AuthorizationInfo info) {
        authorizationValidate(permission);
        super.checkPermission(permission, info);
    }

    @Override
    protected boolean[] isPermitted(List<Permission> permissions, AuthorizationInfo info) {
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
                authorizationValidate(permission);
            }
        }
        return super.isPermitted(permissions, info);
    }

    @Override
    public boolean isPermitted(PrincipalCollection principals, Permission permission) {
        authorizationValidate(permission);
        return super.isPermitted(principals, permission);
    }

    @Override
    protected boolean isPermittedAll(Collection<Permission> permissions, AuthorizationInfo info) {
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
                authorizationValidate(permission);
            }
        }
        return super.isPermittedAll(permissions, info);
    }

    /**
     * 授权验证方法
     * @param permission
     */
    private void authorizationValidate(Permission permission){
        // 模块授权预留接口
    }


    /**
     * 将权限对象中的权限code取出.
     * @param permissions
     * @return
     */
//  public Set<String> getStringPermissions(Set<SysPermission> permissions){
//     Set<String> stringPermissions = new HashSet<String>();
//     if(permissions != null){
//         for(SysPermission p : permissions) {
//            stringPermissions.add(p.getPermission());
//          }
//     }
//       return stringPermissions;
//  }

}
