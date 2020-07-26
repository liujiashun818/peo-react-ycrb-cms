package cn.people.one.appapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author YX
 * @date 2018/10/15
 * @comment
 */
@Data
public class LiveUserVO {
	
	@ApiModelProperty(value = "嘉宾id")
    private Long id;//ID
	
	@ApiModelProperty(value = "名称")
    private String name;//名称
	
	@ApiModelProperty(value = "角色类型（1主持人 2嘉宾）")
    private String role;//角色类型（1主持人 2嘉宾）
	
	@ApiModelProperty(value = "介绍")
    private String description;//介绍
	
	@ApiModelProperty(value = "图片")
    private String image;//图片
}
