package cn.people.one.config;

import cn.people.one.modules.shiro.Credentials.MyMatcher;
import cn.people.one.modules.shiro.filter.MyFormAuthenticationFilter;
import cn.people.one.modules.shiro.realm.MyAuthorizingRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Conditional(NoSSOCondition.class)
@Configuration
public class ShiroConfiguration {

    @Value("${credential.hashAlgorithm}")
    private String hashAlgorithm;
    @Value("${credential.hashInteration}")
    private int hashInteration;

    /**
     * 身份认证realm;
     * (这个需要自己写，账号密码校验；权限等)
     */
    @Bean(name = "myShiroRealm")
    public MyAuthorizingRealm myAuthorizingRealm() {
        MyAuthorizingRealm myAuthorizingRealm = new MyAuthorizingRealm();
        myAuthorizingRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myAuthorizingRealm;
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm.
        securityManager.setRealm(myAuthorizingRealm());
        //注入缓存管理器;//这个如果执行多次，也是同样的一个对象;
        securityManager.setCacheManager(ehCacheManager());
        //注入记住我管理器;
        securityManager.setRememberMeManager(rememberMeManager());
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    /**
     * 凭证匹配器
     * 自己实现的匹配器，继承自HashedCredentialsMatcher
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     * 所以我们需要修改下doGetAuthenticationInfo中的代码;
     * ）
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        MyMatcher myMatcher = new MyMatcher();
        myMatcher.setHashAlgorithmName(hashAlgorithm);//散列算法:这里使用SHA-1算法;
        myMatcher.setHashIterations(hashInteration);//散列的次数，比如散列两次，相当于 SHA-1(SHA-1(""));
        return myMatcher;
    }


    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * 注意：单独一个ShiroFilterFactoryBean配置是或报错的，以为在
     * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
     * <p/>
     * Filter Chain定义说明
     * 1、一个URL可以配置多个Filter，使用逗号分隔
     * 2、当设置多个过滤器时，全部验证通过，才视为通过
     * 3、部分过滤器可指定参数，如perms，roles
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        shiroFilterFactoryBean.setLoginUrl("/auth/login");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        //获取filters，设置过滤器
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("authc", new MyFormAuthenticationFilter());


        //拦截器.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.put("/", "user");
        filterChainDefinitionMap.put("/*.html", "anon");
        filterChainDefinitionMap.put("/*.js", "anon");
        filterChainDefinitionMap.put("/*.css", "anon");
        filterChainDefinitionMap.put("/api/**", "anon"); //暂时放开该接口不进行登录限制，后台权限配置完备之后再进行控制
        filterChainDefinitionMap.put("/api/users/user/current", "anon");//获取当前用户接口
        filterChainDefinitionMap.put("/api/redis/clear", "anon");
        filterChainDefinitionMap.put("/api/init", "anon");
        filterChainDefinitionMap.put("/api/clear", "anon");
        filterChainDefinitionMap.put("/api/front", "anon");
        filterChainDefinitionMap.put("/api/search/article/**", "anon");
        filterChainDefinitionMap.put("/auth/logout", "anon");
        filterChainDefinitionMap.put("/health", "anon");
        filterChainDefinitionMap.put("/api/anon/article", "anon");
        filterChainDefinitionMap.put("/captcha/captcha.jpg","anon");
        filterChainDefinitionMap.put("/api/ueditor/**", "anon");
        filterChainDefinitionMap.put("/static/logo.abc7bc4f.png", "anon");
        filterChainDefinitionMap.put("/antd-iconfont/**", "anon");
        filterChainDefinitionMap.put("/restapi/notifyChronous", "anon");//厨房对接
        filterChainDefinitionMap.put("/restapi/getColumnByUserName", "anon");//厨房对接
        filterChainDefinitionMap.put("/dashboard.html", "user");//配置记住我或认证通过可以访问的地址
        filterChainDefinitionMap.put("/**", "authc");

        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/index");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    /**
     * shiro缓存管理器;
     * 需要注入对应的其它的实体类中：
     * 1、安全管理器：securityManager
     * 可见securityManager是整个shiro的核心；
     */
    @Bean
    public EhCacheManager ehCacheManager() {
        log.info("ShiroConfiguration.getEhCacheManager()");
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:config/ehcache-shiro.xml");
        return cacheManager;
    }


    /**
     * cookie对象;
     *
     * @return
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        log.info("ShiroConfiguration.rememberMeCookie()");
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        simpleCookie.setMaxAge(259200);
        return simpleCookie;
    }

    /**
     * cookie管理对象;
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        log.info("ShiroConfiguration.rememberMeManager()");
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        return cookieRememberMeManager;
    }


}
