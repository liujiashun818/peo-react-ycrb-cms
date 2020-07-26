package cn.people.one.modules.cms.model.front;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by sunday on 2018/10/16.
 */
@Data
public class TagsVO extends BaseEntity {

	@ApiModelProperty(value = "标签名称")
    private String tags;//标签名称
	
	@ApiModelProperty(value = "文章个数")
    private Integer count;//文章个数
}
