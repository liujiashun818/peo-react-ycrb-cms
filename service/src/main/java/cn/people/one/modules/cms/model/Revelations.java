package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.cms.model.front.ArticleMediaVO;
import cn.people.one.modules.cms.model.front.MediaResourceVO;
import cn.people.one.modules.cms.model.type.SysCodeType;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 新闻爆料
 */
@Table("cms_revelations")
@Data
public class Revelations extends BaseEntity {
    private static final long serialVersionUID = 7052581576357789866L;
    @Column(hump = true)
    @ColDefine(width = 255, notNull = true)
    @Comment("用户id")
    @ApiModelProperty(value = "用户id")
    private String userId; // 用户ID
    
    @Column
    @ColDefine(width = 255, notNull = true)
    @Comment("姓名")
    @ApiModelProperty(value = "联系人姓名")
    private String name;    // 联系人姓名
    
    @Column
    @ColDefine(width = 255, notNull = true)
    @Comment("标题")
    @ApiModelProperty(value = "标题")
    private String title;   //标题
    
    @Column
    @ColDefine(width = 1000, notNull = true)
    @Comment("图片")
    @ApiModelProperty(value = "图片")
    private String medias;//图片
    
    @Column
    @ColDefine(width = 1000, notNull = true)
    @Comment("内容")
    @ApiModelProperty(value = "内容")
    private String content;//内容
    
    @Column(hump = true)
    @ColDefine(width = 255, notNull = true)
    @Comment("联系方式")
    @ApiModelProperty(value = "联系方式")
    private String contactInfo;//联系方式
    
    @Column
    @ColDefine(width = 255, notNull = true)
    @Comment("ip")
    private String ip;//ip

    @Column
    @ColDefine(width = 255, notNull = true)
    @Comment("地理位置")
    @ApiModelProperty(value = "地理位置")
    private String address;//address
    
    @Column
    @ColDefine(width = 255, notNull = false)
    @Comment("备注")
    @ApiModelProperty(value = "备注")
    protected String remarks;   // 备注
    public Revelations() {
        super();
    }
}
