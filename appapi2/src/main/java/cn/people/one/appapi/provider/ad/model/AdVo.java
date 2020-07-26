package cn.people.one.appapi.provider.ad.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class AdVo {
	
	@ApiModelProperty(value = "id")
    private String app_key;

	@ApiModelProperty(value = "频道数")
    private String channel_num;

	@ApiModelProperty(value = "客户端代码")
    private String client_code;

	@ApiModelProperty(value = "客户端版本")
    private String client_ver;

    @ApiModelProperty(value = "手机型号")
    private String device_model;

    @ApiModelProperty(value = "手机系统版本")
    private String device_os;

    @ApiModelProperty(value = "厂商")
    private String device_product;

    @ApiModelProperty(value = "分辨率")
    private String device_size;

    @ApiModelProperty(value = "手机平台")
    private String platform;

    @ApiModelProperty(value = "提交时间戳")
    private String posttimestamp;

    @ApiModelProperty(value = "一个加密key,由若干参数构成")
    private String signature;

    @ApiModelProperty(value = "手机唯一标示")
    private String udid;

    private Object data;
}
