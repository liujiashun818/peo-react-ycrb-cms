package cn.people.one.modules.live.model;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
* 直播嘉宾/主持人
* @author cheng
*/
@ApiModel
@Table("live_user")
@Data
public class LiveUser extends BaseEntity {

	@ApiModelProperty(value = "姓名")
    @Column
    @ColDefine(width = 200)
    @Comment("姓名")
    private String name;

	@ApiModelProperty(value = "角色 主持人host 嘉宾guest")
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @Comment("角色 主持人host 嘉宾guest")
    private String role;

	@ApiModelProperty(value = "头像url")
    @Column
    @ColDefine(width = 500)
    @Comment("头像url")
    private String image;

	@ApiModelProperty(value = "简介")
    @Column
    @ColDefine(width = 1000)
    @Comment("简介")
    private String description;

}