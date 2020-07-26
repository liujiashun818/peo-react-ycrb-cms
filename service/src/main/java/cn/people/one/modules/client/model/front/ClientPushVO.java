package cn.people.one.modules.client.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author YX
 * @date 2018/10/9
 * @comment
 */
@Data
public class ClientPushVO {

	@ApiModelProperty(value = "当前页")
    private Integer pageNumber=1;//当前页
	
	@ApiModelProperty(value = "每页显示条数")
    private Integer pageSize=20;//每页显示条数
	
	@ApiModelProperty(value = "关键字")
    private String keyWords;//关键字
	
	@ApiModelProperty(value = "时间")
    private String time;//时间
	
	@ApiModelProperty(value = "创建时间，开始")
    private String createAtStart;
	
	@ApiModelProperty(value = "创建时间，结束")
    private String createAtEnd;
	
	
}
