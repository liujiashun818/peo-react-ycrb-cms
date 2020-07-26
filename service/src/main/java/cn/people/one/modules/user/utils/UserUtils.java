package cn.people.one.modules.user.utils;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.SpringContextHolder;
import cn.people.one.core.util.text.EncodeUtil;
import cn.people.one.core.util.text.EscapeUtil;
import cn.people.one.core.util.text.HashUtil;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.sys.model.Menu;
import cn.people.one.modules.sys.service.IMenuService;
import cn.people.one.modules.user.model.Role;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.lang.Lang;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 用户工具类，为角色机构菜单等提供方法支持
 * Created by cuiyukun on 2017/3/7.
 */
@Slf4j
public class UserUtils {

    private static Integer HASH_INTERATION = 1024;
    private static Integer SALT_SIZE = 8;
    private static String REGEX = "^(?![0-9]+$)(?![a-zA-Z]+$)[A-Za-z0-9~!/@#$%^&*()-_=+\\\\|[{}];:\\'\\\",<.>/?]{8,}$";
    private static String  PREFIX_LOGIN_COUNT="LOGIN_COUNT_";
    private static String  PREFIX_LOGIN_ACCOUNT="LOGIN_ACCOUNT_";
    private static Integer PREFIX_LOGINFAIL_TIME_LIMIT=5;
    private static Integer PREFIX_LOGIN_TIME_LIMIT=30;

    private static BaseDao dao = SpringContextHolder.getBean(BaseDao.class);
    private static IMenuService menuService = SpringContextHolder.getBean(IMenuService.class);
    private static IUserService userService = SpringContextHolder.getBean(IUserService.class);
    private static String sso = SpringContextHolder.getApplicationContext().getEnvironment().getProperty("one.sso");
    private static RedisTemplate<String,String> redisTemplate=SpringContextHolder.getBean("redisTemplate");

    /**
     * 超级管理员获取全部权限
     * @return
     */
    private static List<String> getSuperPermissions() {
        List<Menu> menus = menuService.query(null, null);
        List<String> permissions = new ArrayList<>();
        menus.forEach(menu -> {
            if(StringUtils.isNotBlank(menu.getPermission())){
                if(!permissions.contains(menu.getPermission())){
                    permissions.add(menu.getPermission());
                }
            }
        });
        return permissions;
    }

    /**
     * 获取当前用户,若为空返回new User()
     * @return
     */
    public static User getUser() {
        Subject subject = null;
        try {
            subject = SecurityUtils.getSubject();
        }catch (Exception e){
            log.error("登录信息异常");
        }
        if(null == subject || null == subject.getPrincipal()){
            return null;
        }
        User user = null;
        if("true".equals(sso)){
            String username = subject.getPrincipal().toString();
            if(StringUtils.isNotBlank(username)){
                user = userService.fetch(username);
            }
        }else {
            user = (User)subject.getPrincipal();
        }
        if (user != null){
            //超级管理员获取全部权限
            if (user.isAdmin()){
                user.setPermissions(getSuperPermissions());
                user.setPassword(null);
                return user;
            }
            getCurrentPermission(user);
            user.setPassword(null);
            return user;
        }
        // 如果没有登录，则返回实例化空的User对象。
        return new User();
    }

    /**
     * 获取当前用户的菜单权限
     * @param user
     * @return
     */
    public static void getCurrentPermission(User user) {
        if (user.isAdmin()) {
            user.setPermissions(getSuperPermissions());
        } else {
            List<Role> roles = getRoles(user);
            Set<Menu> menus = new HashSet<>();
            if(Lang.isEmpty(roles)){
                user.setPermissions(null);
            }
            for (Role role : roles) {
                dao.fetchLinks(role, "menus");
                menus.addAll(role.getMenus());
            }
            List<String> permissions = new ArrayList<>();
            menus.forEach(menu -> {
                if(StringUtils.isNotBlank(menu.getPermission())){
                    if(!permissions.contains(menu.getPermission())){
                        permissions.add(menu.getPermission());
                    }
                }
            });
            user.setPermissions(permissions);
        }
    }

    public static List<Role> getRoles(User user){
        if(user!=null){
            user = dao.fetchLinks(user, "roleList");
            List<Role> roles = user.getRoleList();
            return roles;
        }
        return null;
    }

    /**
     * 验证密码格式
     * @param password
     * @return
     */
    public static boolean passwordFormat(String password) {
        if (password.matches(REGEX)) {
            return true;
        }
        return false;
    }

    /**
     * 密码加密
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     */
    public static String encodePassword(String password) {
        String plain = EscapeUtil.unescapeHtml(password);
        byte[] salt = HashUtil.generateSalt(SALT_SIZE);
        byte[] hashPassword = HashUtil.sha1(plain.getBytes(), salt, HASH_INTERATION);
        return EncodeUtil.encodeHex(salt)+EncodeUtil.encodeHex(hashPassword);
    }

    /**
     * 验证密码
     * @param password 明文密码
     * @param oriPassword 密文密码
     * @return
     */
    public static int validatePassword(String password, String oriPassword) {
        byte[] salt = EncodeUtil.decodeHex(oriPassword.substring(0,16));
        byte[] hashPassword = HashUtil.sha1(password.getBytes(), salt, HASH_INTERATION);
        if (EncodeUtil.encodeHex(hashPassword).equals(oriPassword.substring(16))) {
            return 0;
        }
        return -1;
    }

    /**
     * 获取用户登录失败次数
     * @param username
     * @return
     */
    public static int getLoginFailCount(String username){
        String key=PREFIX_LOGIN_COUNT+username;
        Integer loginFailCount=0;
        try {
            String str=redisTemplate.opsForValue().get(key);
            if(StringUtils.isNotBlank(str)){
                loginFailCount=Integer.valueOf(str.trim());
                log.info("失败"+loginFailCount+"次啦!");
                return loginFailCount;
            }
        } catch (NumberFormatException e) {
            return loginFailCount;
        }
        return loginFailCount;
    }

    /**
     * 设置用户登录失败次数
     * @param username
     * @return
     */
    public static int setLoginFailCount(String username){
        String key=PREFIX_LOGIN_COUNT+username;
        Integer loginFailCount=0;
        try {
            String str=redisTemplate.opsForValue().get(key);
            if(StringUtils.isBlank(str)){
                loginFailCount++;
                redisTemplate.opsForValue().set(key,loginFailCount.toString(),PREFIX_LOGINFAIL_TIME_LIMIT,TimeUnit.MINUTES);
            }else{
                loginFailCount=Integer.parseInt(str.trim());
                loginFailCount++;
                long expireTime=redisTemplate.getExpire(key);
                log.info("剩余时间："+expireTime);
                if(expireTime>100){
                    redisTemplate.opsForValue().set(key,loginFailCount.toString(),expireTime,TimeUnit.SECONDS);
                }
            }
            return loginFailCount;
        } catch (Exception e) {
            log.error("设置登录失败次数出错",e);
            return loginFailCount;
        }
    }

    /**
     * 清除登录失败次数缓存记录
     * @param username
     */
    public static void clearLoginFailCount(String username){
        String key=PREFIX_LOGIN_COUNT+username;
        redisTemplate.delete(key);
    }

    /**
     * 设置用户禁用时间
     * @param username
     */
    public static void setLoginAccount(String username){
        String key=PREFIX_LOGIN_ACCOUNT+username;
        redisTemplate.opsForValue().set(key,username,PREFIX_LOGIN_TIME_LIMIT,TimeUnit.MINUTES);
    }

    /**
     * 获取禁用用户
     * @param username
     * @return
     */
    public static String getLoginAccount(String username){
        String key=PREFIX_LOGIN_ACCOUNT+username;
        return redisTemplate.opsForValue().get(key);
    }
}
