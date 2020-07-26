package cn.people.one.appapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wilson on 2018-10-11.
 */
@Data
public class ArticleMetaVO {
	
	@ApiModelProperty(value = "字段编号")
    private String fieldCode;
	
	@ApiModelProperty(value = "文章id")
    private Long articleId;
	
	@ApiModelProperty(value = "字段值")
    private String fieldValue;
}
