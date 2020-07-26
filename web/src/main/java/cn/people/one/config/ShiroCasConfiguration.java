package cn.people.one.config;

import cn.people.one.modules.shiro.realm.MyCasRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: 张新征
 * Date: 2017/5/10 15:56
 * Description:
 */
@Conditional(SSOCondition.class)
@Configuration
public class ShiroCasConfiguration {
    private static final String casFilterUrlPattern = "/shiro-cas";

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager,
                                                            CasFilter casFilter,
                                                            @Value("${cas.server}") String casServerUrlPrefix,
                                                            @Value("${cas.client}") String casClientUrlPrefix) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        String loginUrl = casServerUrlPrefix + "/login?service=" + casClientUrlPrefix + casFilterUrlPattern;
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        shiroFilterFactoryBean.setSuccessUrl("/cms.html");
        Map<String, Filter> filters = new HashMap<>();
        filters.put("casFilter", casFilter);
        shiroFilterFactoryBean.setFilters(filters);

        loadShiroFilterChain(shiroFilterFactoryBean);
        return shiroFilterFactoryBean;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        return filterRegistration;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(MyCasRealm casRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(casRealm);
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());
        securityManager.setSubjectFactory(new CasSubjectFactory());
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean) {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        filterChainDefinitionMap.put(casFilterUrlPattern, "casFilter");
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/*.html", "anon");
        filterChainDefinitionMap.put("/*.js", "anon");
        filterChainDefinitionMap.put("/*.css", "anon");
        filterChainDefinitionMap.put("/api/users/user/current", "anon");//获取当前用户接口
        filterChainDefinitionMap.put("/api/redis/clear","anon");
        filterChainDefinitionMap.put("/api/init","anon");
        filterChainDefinitionMap.put("/api/clear","anon");
        filterChainDefinitionMap.put("/api/front","anon");
        filterChainDefinitionMap.put("/api/search/article/**","anon");
        filterChainDefinitionMap.put("/auth/logout", "anon");
        filterChainDefinitionMap.put("/health", "anon");
        filterChainDefinitionMap.put("/api/anon/article", "anon");
        filterChainDefinitionMap.put("/dashboard.html", "user");//配置记住我或认证通过可以访问的地址
        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    }

    /**
     * CAS Filter
     */
    @Bean(name = "casFilter")
    public CasFilter getCasFilter(@Value("${cas.server}") String casServerUrlPrefix,
                                  @Value("${cas.client}") String casClientUrlPrefix) {
        CasFilter casFilter = new CasFilter();
        casFilter.setName("casFilter");
        casFilter.setEnabled(true);
        String loginUrl = casServerUrlPrefix + "/login?service=" + casClientUrlPrefix + casFilterUrlPattern;
        casFilter.setFailureUrl(loginUrl);
        return casFilter;
    }

}
