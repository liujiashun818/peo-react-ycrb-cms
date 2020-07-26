package cn.people.one.appapi.provider.ad.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wilson on 2018-10-29.
 */
@Data
public class AdImage {
	
	@ApiModelProperty(value = "描述")
    private String desc;
	
	@ApiModelProperty(value = "尺寸")
    private Integer size;
	
	@ApiModelProperty(value = "图片地址")
    private String image;
}
