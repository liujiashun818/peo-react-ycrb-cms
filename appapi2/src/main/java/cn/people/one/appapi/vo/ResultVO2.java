package cn.people.one.appapi.vo;

import cn.people.one.appapi.model.ErrorMessage;
import cn.people.one.core.base.api.Result;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wilson on 2018-10-09.
 */
@ApiModel(value = "Result", description = "返回响应数据")
public class ResultVO2<T> {

    @ApiModelProperty(value = "服务状态码 "+Result.DEFAULT_CODE_SUCCESS+"：成功...")
    private int code;

    @ApiModelProperty(value = "信息")
    private String msg;

    @ApiModelProperty(value = "数据")
    private T item;

    public static <T> ResultVO2<T> result(ErrorMessage message) {
        ResultVO2<T> rj = new ResultVO2<T>();
        rj.setCode(message.getCode());
        rj.setMsg(message.getMsg());
        return rj;
    }

    public static <T> ResultVO2<T> result(int code, String message) {
        ResultVO2<T> rj = new ResultVO2<T>();
        rj.setCode(code);
        rj.setMsg(message);
        return rj;
    }

    public static <T> ResultVO2<T> error(String message) {
        return ResultVO2.result(Result.DEFAULT_CODE_ERROR, message);
    }

    public static <T> ResultVO2<T> success(T item) {
        return ResultVO2.success(item,"result");
    }

    public static <T> ResultVO2<T> success(T item,String msg) {
        ResultVO2<T> rj = new ResultVO2<>();
        rj.setCode(Result.DEFAULT_CODE_SUCCESS);
        rj.setMsg(msg);
        rj.setItem(item);
        return rj;
    }
//
//    public ResultVO2<T>(ErrorMessage message) {
//        this.code = message.getCode();
//        this.msg = message.getMsg();
//    }
//
//    public ResultVO2<T>(int code, String msg) {
//        this.code = code;
//        this.msg = msg;
//    }
//
//    public <T> ResultVO2<T>(T item) {
//        this.code = 0;
//        this.msg = "result";
//        this.item = item;
//    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}
