package cn.people.one.modules.user.model;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;
@ApiModel
@Table("sys_user")
@Data
public class User extends BaseEntity {

    private static final long serialVersionUID = -969706516931557499L;

    @ApiModelProperty(value = "登录名")
    @Column
    @ColDefine(width = 100)
    @Comment("登录名")
    @Name
    private String username;

    @ApiModelProperty(value = "姓名")
    @Column
    @ColDefine(width = 100)
    @Comment("姓名")
    private String name;

    @ApiModelProperty(value = "密码")
    @Column
    @ColDefine(width = 100)
    @Comment("密码")
    private String password;

    /**
     * 用户修改新密码
     */
    @ApiModelProperty(value = "用户修改新密码")
    private String newPassword;

    @ApiModelProperty(value = "ApiOperation")
    @Column
    @ColDefine(width = 100)
    @Comment("备注")
    private String remark;

    @ApiModelProperty(value = "ApiOperation")
    @Column(hump = true)
    @ColDefine(type = ColType.INT)
    @Comment("机构ID")
    private Long officeId;

    /**
     * 用户中心用户ID
     */
    @ApiModelProperty(value = "用户中心用户ID")
    @Column(hump = true)
    @ColDefine(type = ColType.VARCHAR)
    @Comment("用户中心用户ID")
    private String upmsUserId;
    /**
     * 用户的角色id列表
     */
    @ApiModelProperty(value = "用户的角色id列表")
    private List<Long> roleIds;

    /**
     * 用户的菜单权限列表
     */
    @ApiModelProperty(value = "用户的菜单权限列表")
    private List<String> permissions;

    @ManyMany(from = "userid", relation = "sys_user_role",target = Role.class, to = "roleid")
    private List<Role> roleList;

    public boolean isAdmin(){
        return isAdmin(getId());
    }

    public boolean isAdmin(Long id){
        return 1L == id;
    }

    public static final String ROLE_LIST = "roleList";

    /**
     * 记住我默认验证码
     */
    public static final String REMEMBERME_VALIDATECODE="rememberme_validateCode";
    public static class Constant{
        public static final String UPMSUSERID="upms_user_id";
        public static final String DEFAULTPASSWORD="12345678901234567890";
        public static final String WRONGPASSWORD="123456";
        public static final String NAME="name";
    }
}