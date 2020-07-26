package cn.people.one.modules.shiro.Credentials;

import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 实现密码对比，区分记住我和正常登录的密码验证
 * Created by cuiyukun on 2017/5/3.
 */
@Component
public class MyMatcher extends HashedCredentialsMatcher {

    @Resource
    private IUserService userService;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isRemembered()) {
            User userinfo = (User)currentUser.getPrincipal();
            String rememberCredential = userinfo.getPassword();
            User user = userService.fetch(userinfo.getUsername());
            return user.getPassword().equals(rememberCredential);
        } else {
            return super.doCredentialsMatch(token, info);
        }
    }
}
