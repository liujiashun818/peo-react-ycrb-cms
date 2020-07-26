package cn.people.one.modules.cms.model.front;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by maliwei.tall on 2017/4/11.
 */
@Data
public class StatsVO extends BaseEntity {
	
	@ApiModelProperty(value = "作者")
    private String authors;//作者
	
	@ApiModelProperty(value = "父栏目id")
    private String parentId;//父栏目id
	
	@ApiModelProperty(value = "父栏目")
    private String parentName;//父栏目
    /**
     * 栏目id
     */
	@ApiModelProperty(value = "栏目id")
    private String categoryId;
	
	@ApiModelProperty(value = "栏目")
    private String categoryName;//栏目
	
	@ApiModelProperty(value = "稿件数")
    private int articleCount;//稿件数
	
	@ApiModelProperty(value = "点击量")
    private int hitsCount;//点击量
	
	@ApiModelProperty(value = "评论数")
    private int commentsCount;//评论数
	
	@ApiModelProperty(value = "最后更新时间")
    private String updateTime;//最后更新时间
}
