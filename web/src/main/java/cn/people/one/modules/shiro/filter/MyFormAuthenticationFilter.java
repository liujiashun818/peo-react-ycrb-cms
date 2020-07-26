package cn.people.one.modules.shiro.filter;

import cn.people.one.core.base.api.Result;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.shiro.captcha.CaptchaException;
import cn.people.one.modules.shiro.loginLock.LoginLockException;
import cn.people.one.modules.shiro.realm.MyUsernamePasswordToken;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证（暂不包含验证码）过滤类
 * <p>
 * Created by cuiyukun on 2017/1/24.
 */
@Slf4j
public class MyFormAuthenticationFilter extends FormAuthenticationFilter {

    private static JsonMapper jsonMapper = JsonMapper.defaultMapper();

    public static final String DEFAULT_CAPTCHA_PARAM = "validateCode";


    /**
     * 默认登录失败次数上限
     */
    public static final Integer DEFAULT_LOGINfAIL_COUNT=3;
    private String captchaParam = DEFAULT_CAPTCHA_PARAM;
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {

         Map<String, String> map = new HashMap<>();
        try {
            String string = StringUtils.getRequestPostStr((HttpServletRequest) request);
            map = JsonMapper.INSTANCE.fromJson(string, Map.class);
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
        String username = map.get("username");
        String password = map.get("password");
        if (password == null) {
            password = "";
        }else{
            try {
                username=new String(Base64.decodeBase64(username),"UTF-8");
                password=new String(Base64.decodeBase64(password),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("解密密码出错",e);
                password="";
            }
        }
        boolean rememberMe;
        if (map.get("rememberMe").equalsIgnoreCase("yes")) {
            rememberMe = true;
        } else {
            rememberMe = false;
        }
        String host = StringUtils.getRemoteAddr((HttpServletRequest) request);
        String captcha = map.get(DEFAULT_CAPTCHA_PARAM);
        return new MyUsernamePasswordToken(username, password.toCharArray(), rememberMe, host, captcha);
    }

    public String getCaptchaParam() {
        return captchaParam;
    }

    protected String getCaptcha(ServletRequest request) {
        return WebUtils.getCleanParam(request, getCaptchaParam());
    }

    /**
     * 表示当访问拒绝时
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        if (this.isLoginRequest(request, response)) {
            if (this.isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Login submission detected.  Attempting to execute login.");
                }

                return this.executeLogin(request, response);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Login page view.");
                }
                WebUtils.issueRedirect(request, response, "/cms.html");
                return false;
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Attempting to access a path which requires authentication.  Forwarding to the Authentication url [" + this.getLoginUrl() + "]");
            }

            if (!"XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest) request)
                .getHeader("X-Requested-With"))) {// 不是ajax请求
                WebUtils.issueRedirect(request, response, "/cms.html");
            } else {
                try {
                    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                    httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println(jsonMapper.toJson(Result.error(Result.PERMISSION_DENIED_CODE_ERROR, "没有权限")));
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    log.error("数据返回异常", e);
                }
            }
            return false;
        }
    }


    /**
     * 当登录成功
     *
     * @param token
     * @param subject
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        // TODO 清除登录失败次数记录
//        UserUtils.clearLoginFailCount(token.getPrincipal().toString());
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
        httpServletResponse.setCharacterEncoding("UTF-8");
        PrintWriter out = httpServletResponse.getWriter();
        User user = UserUtils.getUser();
        user.setRoleList(null);
        out.println(jsonMapper.toJson(Result.success(user)));
        out.flush();
        out.close();
        return false;
    }

    /**
     * 当登录失败
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException exception, ServletRequest request, ServletResponse response) {
        if (!"XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest) request)
            .getHeader("X-Requested-With"))) {// 不是ajax请求
            setFailureAttribute(request, exception);
            return true;
        }

        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setHeader("Content-type", "applicatoin/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            String message = exception.getClass().getSimpleName();
            System.out.println(CaptchaException.class.getName());
            System.out.println(message);
            System.out.println(exception);
            if ("IncorrectCredentialsException".equals(message)|| "UnknownAccountException".equals(message)) {
                out.println(jsonMapper.toJson(Result.error("账号或密码错误")));
                //TODO 判断是否已经登录失败超限
                /*int loginFailCount=UserUtils.getLoginFailCount(token.getPrincipal().toString());
                if(loginFailCount>=DEFAULT_LOGINfAIL_COUNT){
                    UserUtils.setLoginAccount(token.getPrincipal().toString());
                    out.println(jsonMapper.toJson(Result.error("输入错误次数过多，为了您的账号安全该账号已经被锁定，请稍后再试")));
                }else {
                    //登录失败次数加1
                    UserUtils.setLoginFailCount(token.getPrincipal().toString());
                    out.println(jsonMapper.toJson(Result.error("账号或密码错误")));
                }*/
            } /*else if ("UnknownAccountException".equals(message)) {
                out.println(jsonMapper.toJson(Result.error("账号不存在")));
            }*/ else if ("LockedAccountException".equals(message)) {
                out.println(jsonMapper.toJson(Result.error("账号被锁定")));
            } else if (LoginLockException.LOGINLOCKEXCEPTION.equals(message)) {
                out.println(jsonMapper.toJson(Result.error("输入错误次数过多，为了您的账号安全该账号已经被锁定，请稍后再试")));
            }else if ("CaptchaException".equals(message)) {
                String exceptionMessage=exception.toString();
                out.println(jsonMapper.toJson(Result.error(CaptchaException.ERROR_CODE,exceptionMessage.substring(exceptionMessage.lastIndexOf(":")+1,exceptionMessage.length()))));
            }else {
                out.println(jsonMapper.toJson(Result.error("未知错误")));
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("数据返回异常", e);
        }
        return false;
    }
}

