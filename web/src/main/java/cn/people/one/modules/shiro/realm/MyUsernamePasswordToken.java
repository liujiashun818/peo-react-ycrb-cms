package cn.people.one.modules.shiro.realm;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 用户和密码（包含验证码）令牌类
 *
 * Created by cuiyukun on 2017/1/24.
 */
public class MyUsernamePasswordToken extends UsernamePasswordToken{

    private String captcha;

    public MyUsernamePasswordToken() {
      super();
    }

    public MyUsernamePasswordToken(String username, char[] password,
                                 boolean rememberMe, String host, String captcha) {
      super(username, password, rememberMe, host);
      this.captcha = captcha;
    }

    public String getCaptcha() {
      return captcha;
    }

    public void setCaptcha(String captcha) {
      this.captcha = captcha;
    }

}
