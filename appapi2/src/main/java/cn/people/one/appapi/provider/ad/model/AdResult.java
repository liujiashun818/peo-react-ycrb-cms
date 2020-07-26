package cn.people.one.appapi.provider.ad.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Created by wilson on 2018-10-26.
 */
@Data
public class AdResult {

    @JSONField(name = "error_code")
    private Integer errorCode;

    @JSONField(name = "error_message")
    private String errorMessage;

    private Integer count;

    private List<Ad> data;

}
