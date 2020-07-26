package cn.people.one.modules.shiro.captcha;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 验证码异常处理类
 */
public class CaptchaException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

    /**
     * 验证码错误码
     */
	public static final Integer ERROR_CODE=13;

	public CaptchaException() {
		super();
	}

	public CaptchaException(String message, Throwable cause) {
		super(message, cause);
	}

	public CaptchaException(String message) {
		super(message);
	}

	public CaptchaException(Throwable cause) {
		super(cause);
	}

}
