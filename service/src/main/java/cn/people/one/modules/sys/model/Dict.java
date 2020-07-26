package cn.people.one.modules.sys.model;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
* 字典
* @author cuiyukun
*/
@ApiModel
@Table("sys_dict")
@Data
public class Dict extends BaseEntity {

	@ApiModelProperty(value = "数据值")
    @Column
    @ColDefine(width = 20)
    @Comment("数据值")
    private String value;	// 数据值

	@ApiModelProperty(value = "标签名")
    @Column
    @ColDefine(width = 20)
    @Comment("标签名")
    private String label;	// 标签名

	@ApiModelProperty(value = "类型")
    @Column
    @ColDefine(width = 200)
    @Comment("类型")
    private String type;	// 类型

	@ApiModelProperty(value = "描述")
    @Column
    @ColDefine(width = 200)
    @Comment("描述")
    private String description;// 描述

    /**
     * 文章中的常量字符
     */
    public static class Constant{
        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String DESCRIPTION= "description";
    }

}