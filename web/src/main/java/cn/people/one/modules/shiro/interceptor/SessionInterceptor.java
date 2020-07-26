package cn.people.one.modules.shiro.interceptor;

import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.shiro.realm.MyUsernamePasswordToken;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 拦截器，对“记住我”功能进行判断处理
 *
 * @author cuiyukun
 */
@Slf4j
public class SessionInterceptor implements HandlerInterceptor {

    @Resource
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
//        User auser = (User) request.getSession().getAttribute("currentUser");
//        if (auser == null) {
//            response.setHeader("Content-type", "application/json;charset=UTF-8");
//            response.setCharacterEncoding("UTF-8");
//            PrintWriter out = response.getWriter();
//            out.println("{\"code\":-6,\"msg\":\"登录过期，请重新登录\"}");
//            out.flush();
//            out.close();
//            return false;
//        }

        log.info("Session拦截器--> SessionInterceptor.preHandle()");
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isRemembered()) {
            try {
                User userinfo = (User) currentUser.getPrincipal();
                User user = userService.fetch(userinfo.getUsername());
                String host = StringUtils.getRemoteAddr(request);
                UsernamePasswordToken token=null;
                if(user!=null && StringUtils.isNotBlank(user.getUpmsUserId())){
                    token = new MyUsernamePasswordToken(user.getUsername(), User.Constant.DEFAULTPASSWORD.toCharArray(), currentUser.isRemembered(), host, User.REMEMBERME_VALIDATECODE);
                }else{
                    token = new MyUsernamePasswordToken(user.getUsername(), user.getPassword().toCharArray(), currentUser.isRemembered(), host, User.REMEMBERME_VALIDATECODE);
                }
                //把当前用户放入session
                currentUser.login(token);
                Session session = currentUser.getSession();
                session.setAttribute("currentUser", user);
                //设置会话的过期时间--ms,默认是30分钟，设置负数表示永不过期
//                session.setTimeout(-1000L);
                session.setTimeout(5 * 60 * 1000L);
            } catch (Exception e) {
                log.error(e.getMessage());
                //自动登录失败,跳转到登录页面
                return false;
            }

            if (!currentUser.isAuthenticated()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("---postHandle---");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("---afterCompletion---");
    }
}
