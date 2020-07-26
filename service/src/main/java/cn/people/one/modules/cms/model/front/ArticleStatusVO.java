package cn.people.one.modules.cms.model.front;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ArticleStatusVO extends BaseEntity {
	
	@ApiModelProperty(value = "姓名")
    private String userName;//姓名
	
	@ApiModelProperty(value = "所属机构")
    private String officeName;//所属机构
	
	@ApiModelProperty(value = "稿件数")
    private int articleCount;//稿件数
	
	@ApiModelProperty(value = "点击量")
    private int hitsCount;//点击量
	
	@ApiModelProperty(value = "评论数")
    private int commentsCount;//评论数
}
