package cn.people.one.modules.ask.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by de'l'l on 2019/2/22.
 */
@Data
public class AskGovernmentType {
	
	@ApiModelProperty(value = "")
    private Long id;
	
	@ApiModelProperty(value = "政府部门名称")
    private String name;
	
	@ApiModelProperty(value = "行政区划编码")
    private String area_code;
	
	@ApiModelProperty(value = "是否有下一级部门：0无  1有  默认为0")
    private String has_child;
	
	@ApiModelProperty(value = "回复总数")
    private String reply_num;
	
	@ApiModelProperty(value = "简称")
    private String short_name;
}
