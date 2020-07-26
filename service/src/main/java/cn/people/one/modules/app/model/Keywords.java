package cn.people.one.modules.app.model;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
* 搜索关键词
* @author Cheng
*/
@Table("app_keywords")
@Data
public class Keywords extends BaseEntity {

    @Column
    @ColDefine(type = ColType.TEXT)
    @Comment("标题")
    @ApiModelProperty(value = "标题")
    private String title;

    @Column
    @ColDefine(type = ColType.TEXT)
    @Comment("备注")
    @ApiModelProperty(value = "备注")
    private String remarks;

}