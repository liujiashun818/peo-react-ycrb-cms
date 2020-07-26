package cn.people.one.appapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wilson on 2018-10-11.
 */
@Data
public class VoteOptionVO {
	
	@ApiModelProperty(value = "id")
    private Long id;
	
	@ApiModelProperty(value = "标题")
    private String title;
	
	@ApiModelProperty(value = "点击数")
    private Integer hits;
}
