package cn.people.one.modules;

import cn.people.one.core.base.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.expression.ExpressionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * User: 张新征
 * Date: 2017/2/10 14:53
 * Description:统一异常处理类
 */
@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = Throwable.class)
    public Result defaultErrorHandler(HttpServletRequest req, Throwable e) {
        if (e instanceof UnauthorizedException) {
            return Result.error(Result.PERMISSION_DENIED_CODE_ERROR, "没有权限");
        }
        if (e instanceof ExpressionException) {
            return Result.success();
        }
        if (e instanceof AuthorizationException) {
            return Result.error(-10000, "登录过期，请重新登录");
        }
        log.error("异常信息："+e);
        return Result.error(Result.EXCEPTION_CODE_ERROR, "系统错误，请您重新登录或联系管理员进一步处理");
    }

}
