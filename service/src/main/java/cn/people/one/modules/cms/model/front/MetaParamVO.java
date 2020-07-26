package cn.people.one.modules.cms.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by lml on 2017/1/10.
 */
@Data
public class MetaParamVO {

    @ApiModelProperty(value = "别名")
    private String slug;

    @ApiModelProperty(value = "文章id")
    private Long articleId;

    @ApiModelProperty(value = "字段编号")
    private String fieldCode;

    @ApiModelProperty(value = "字段值")
    private String fieldValue;
}
