package cn.people.one.modules.base.entity;

import cn.people.one.modules.user.model.User;
import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.entity.annotation.*;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

import java.beans.Transient;
import java.io.Serializable;

/**
 * 基础应用实体类
 */
@Data
@NoArgsConstructor
public abstract class BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Expose
    protected transient Long id;
    public BaseModel(Long id){
        this.id=id;
    }

    public void init(){
        this.delFlag=STATUS_ONLINE;
    }

    @Transient
    public boolean isNew() {
        return this.id == null;
    }
    @Column(hump = true)
    @Comment("创建人")
    @Prev(els = @EL("$me.uid()"))
    @ColDefine(type = ColType.INT)
    @ApiModelProperty(value = "创建人")
    private Long createBy;

    @Column(hump = true)
    @Comment("创建时间")
    @Prev(els = @EL("$me.createAt()"))
    @ApiModelProperty(value = "创建时间")
    private Long createAt;

    @Column(hump = true)
    @Comment("最后修改人")
    @Prev(els = @EL("$me.uid()"))
    @ColDefine(type = ColType.INT)
    @ApiModelProperty(value = "最后修改人")
    private Long updateBy;

    @Column(hump = true)
    @Comment("最后修改时间")
    @Prev(els = @EL("$me.updateAt()"))
    @ApiModelProperty(value = "最后修改时间")
    private Long updateAt;

    @Column(
            hump = true
    )
    @Comment("状态标记(0：正常；1：下线；2：审核；3：删除；4:审核未通过)")
    @ColDefine(
            type = ColType.INT,
            width = 1
    )

    @ApiModelProperty(value = "状态标记(0：正常；1：下线；2：审核；3：删除；4:审核未通过)")
    private Integer delFlag;

    public static final String FIELD_STATUS = "del_flag";
    public static final int STATUS_ONLINE = 0;
    public static final int STATUS_OFFLINE = 1;
    public static final int STATUS_AUDIT = 2;
    public static final int STATUS_DELETE = 3;
    public static final int STATUS_NO_AUDIT = 4;

    public String toString() {
        return String.format("/*%s*/%s", super.toString(), Json.toJson(this, JsonFormat.compact()));
    }

    public Long createAt() {
        if(null != this.getCreateAt()){
            return this.getCreateAt();
        }
        return System.currentTimeMillis();
    }

    public Long updateAt() {
        return System.currentTimeMillis();
    }

    public Long uid() {
        try {
            Subject subject = SecurityUtils.getSubject();
            User user = (User) subject.getPrincipal();
            return user == null ? null : user.getId();
        } catch (Exception e) {
            return null;
        }
    }

    public static class Constant{
        public static final String CREATE_AT="create_at";
        public static final String UPDATE_AT="update_at";
        public static final String SYS_CODE="sys_code";
        public static final String CATEGORY_ID="category_id";
    }
}
