package cn.people.one.core.base.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Api返回结果
 */
@Data
@ApiModel(value = "Result",description = "返回响应数据")
public class Result<T> {

    public static final int DEFAULT_CODE_SUCCESS = 0;
    public static final int DEFAULT_CODE_ERROR = -1;//可以交互的错误，错误信息给用户展示
    public static final int EXCEPTION_CODE_ERROR = -2;//系统异常，给前端返回异常信息，前端不展现给用户，只提示系统错误请联系管理员
    public static final int NOT_LOGGED_IN_CODE_ERROR = -3;//没有登录
    public static final int PERMISSION_DENIED_CODE_ERROR = -4;//没有权限
    public static final String DEFAULT_MSG_SUCCESS = "ok";

    @ApiModelProperty(value = "服务状态码 "+ Result.DEFAULT_CODE_SUCCESS+"：成功...")
    private int code;
    @ApiModelProperty(value = "信息")
    private String msg;
    @ApiModelProperty(value = "数据")
    private T data;

    public Result() {
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result(DEFAULT_CODE_SUCCESS, DEFAULT_MSG_SUCCESS, null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> re = new Result<T>();
        re.code = DEFAULT_CODE_SUCCESS;
        re.msg = DEFAULT_MSG_SUCCESS;
        re.data = data;
        return re;
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result(DEFAULT_CODE_SUCCESS, msg, data);
    }

    public static <T> Result<T> error(int code, String msg) {
        return new Result(code, msg, null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result(DEFAULT_CODE_ERROR, msg, null);
    }
}
