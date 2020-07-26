package cn.people.one.modules.shiro.realm;

import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import cn.people.one.modules.user.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * User: 张新征
 * Date: 2017/5/10 16:16
 * Description:
 */
@Component
@Slf4j
public class MyCasRealm extends CasRealm{

    @Value("${cas.server}")
    private String casServer;

    @Value("${cas.client}")
    private String casClient;

    @Autowired
    private IUserService userService;

    @PostConstruct
    public void initProperty(){
        setCasServerUrlPrefix(casServer);
        // 客户端回调地址
        setCasService(casClient+"/shiro-cas");
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String)super.getAvailablePrincipal(principals);
        //到数据库查是否有此对象
        User user = userService.fetch(username);
        if(user != null){
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            UserUtils.getCurrentPermission(user);
            info.addStringPermissions(user.getPermissions());
            return info;
        }
        // 返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
        return null;
    }
}
