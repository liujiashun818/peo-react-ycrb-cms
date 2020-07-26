package cn.people.one.modules.shiro.loginLock;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 登录锁定
 * @author YX
 * @date 2019-06-27
 * @comment
 */
public class LoginLockException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    /**
     * 登陆锁定异常
     */
    public static final String LOGINLOCKEXCEPTION="LoginLockException";

    public LoginLockException(String message) {
        super(message);
    }

    public LoginLockException() {
        super();
    }
}
