package cn.people.one.modules.client.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信
 * Created by sunday on 2018/10/24.
 */
@Data
public class WechatVO {

	@ApiModelProperty(value = "appId")
    private String appId;
	@ApiModelProperty(value = "appId")
    private String timestamp;
	@ApiModelProperty(value = "随机字符串")
    private String nonceStr;
	@ApiModelProperty(value = "签名")
    private String signature;
	@ApiModelProperty(value = "地址")
    private String url;
	@ApiModelProperty(value = "访问令牌")
    private String accessToken;
}
