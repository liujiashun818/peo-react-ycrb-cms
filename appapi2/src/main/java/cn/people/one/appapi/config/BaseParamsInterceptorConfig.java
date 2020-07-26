package cn.people.one.appapi.config;

import cn.people.one.appapi.interceptor.BaseParamsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 用于设置基本参数拦截
 * <p>
 * Created by wilson on 2018-10-09.
 */
@Configuration
public class BaseParamsInterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private BaseParamsInterceptor baseParamsInterceptor;

    /**
     * 在这里设置要拦截的路径和不拦截的路径
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseParamsInterceptor)
                .addPathPatterns("/**") // 拦截
                .excludePathPatterns("/check"); // 不拦截
    }

}
