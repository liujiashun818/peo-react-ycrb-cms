package cn.people.one.modules.user.model;

import cn.people.one.modules.base.entity.TreeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * 机构类
 */
@ApiModel
@Table("sys_office")
@Data
public class Office extends TreeEntity<Office> {

	@ApiModelProperty(value = "机构名称")
    @Column
    @ColDefine(width = 100)
    @Comment("机构名称")
    @Name
    private String name;

	@ApiModelProperty(value = "备注")
    @Column
    @ColDefine(width = 100)
    @Comment("备注")
    private String remark;

	@ApiModelProperty(value = "别名")
    @Column
    @ColDefine(width = 100)
    @Comment("别名")
    private String slug;

	@ApiModelProperty(value = "排序字段")
    @Column
    @Comment("排序字段")
    private Integer sort;

	@ApiModelProperty(value = "用户中心机构ID")
    @Column(hump = true)
    @Comment("用户中心机构ID")
    @ColDefine(type = ColType.VARCHAR)
    private String upmsOfficeId;

    public static class Constant{
        public static final String UPMSOFFICEID="upms_office_id";
    }
}
