package cn.people.one.appapi.model;

import lombok.Data;

/**
 * Created by wilson on 2018-10-11.
 */
@Data
public class ErrorMessage {
    private Integer code;
    private String msg;

    private ErrorMessage(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ErrorMessage error(Integer code, String msg) {
        return new ErrorMessage(code, msg);
    }
}
