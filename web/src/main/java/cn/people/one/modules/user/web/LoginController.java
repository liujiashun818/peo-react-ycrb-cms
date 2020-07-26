package cn.people.one.modules.user.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.shiro.captcha.CaptchaException;
import cn.people.one.modules.shiro.loginLock.LoginLockException;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.google.code.kaptcha.Producer;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cuiyukun on 2017/1/17.
 */
@Api(description = "登录(user模块)")
@RestController
@Slf4j
public class LoginController {

    @Value("${cas.server}")
    private String casServer;

    @Value("${cas.client}")
    private String casClient;

    @Value("${theone.sso}")
    private Boolean sso;

    @Autowired
    private Producer producer;

    @ApiOperation("登录接口")
	@RequestMapping(value = "/auth/login", method = RequestMethod.GET)
	public Result login(HttpServletRequest request, HttpServletResponse response) {
		Subject subject = SecurityUtils.getSubject();
		if (subject.getPrincipal() != null) {
			return index(request, response);
		}
		return Result.error(Result.NOT_LOGGED_IN_CODE_ERROR,"用户未登录");
	}

    @ApiOperation("登出接口")
    @RequestMapping(value = "/auth/logout", method = RequestMethod.GET)
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityUtils.getSubject().logout();
        if(sso){
            Map<String, String> map = new HashMap<>();
            map.put("direct", casServer + "/logout?service=" + casClient + "/shiro-cas");
            return Result.success(map);
        }else {
            return Result.success();
        }
    }

    @ApiOperation("登录接口")
	@RequestMapping(value = "/auth/login", method = RequestMethod.POST)
	public Result login(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) {
		// 登录失败从request中获取shiro处理的异常信息
		// shiroLoginFailure:就是shiro异常类的全类名
		String exception = (String) request.getAttribute("shiroLoginFailure");
		String msg = "";
		if (exception != null) {
			if (UnknownAccountException.class.getName().equals(exception)) {
				msg = "UnknownAccountException -->帐号不存在：";
			} else if (IncorrectCredentialsException.class.getName().equals(exception)) {
				msg = "IncorrectCredentialsException -- > 密码不正确：";
			} else if (CaptchaException.class.getName().equals(exception)) {
				msg = "验证码错误";
				return Result.error(CaptchaException.ERROR_CODE,msg);
			}else if (LoginLockException.LOGINLOCKEXCEPTION.equals(exception)) {
                msg="输入错误次数过多，为了您的账号安全该账号已经被锁定，请稍后再试";
            } else {
				msg = "else -- > " + exception;
			}
			log.warn(msg);

			return Result.error(msg);
		}
		map.put("msg", msg);

		Subject subject = SecurityUtils.getSubject();
		Object principal = subject.getPrincipal();
		if (principal != null) {
			return index(request, response);
		}
		return Result.error("登录失败");
	}

	/**
	 * 登录成功，进入管理首页
	 */
    @ApiOperation("登录成功，进入管理首页")
	@RequestMapping(value = "/index")
	public Result<User> index(HttpServletRequest request, HttpServletResponse response) {
        User user = UserUtils.getUser();
        user.setRoleList(null);
		return Result.success(user);
	}

    /**
     * 生成验证码
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @ApiOperation("生成验证码")
    @RequestMapping("/captcha/captcha.jpg")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        request.getSession().setAttribute("validateCode",text);
        String validateCode = (String)request.getSession().getAttribute("validateCode");
        log.info("validateCode===" + validateCode);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
    }
}
