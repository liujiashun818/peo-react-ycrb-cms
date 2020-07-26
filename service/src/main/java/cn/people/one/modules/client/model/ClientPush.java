package cn.people.one.modules.client.model;

import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;
import org.nutz.dao.entity.annotation.*;

/**
 * Created by lml on 17-2-27.
 * 手机推送
 */
@Data
@Table("client_push")
@NoArgsConstructor
public class ClientPush extends BaseEntity {

    @Column
    @Comment("描述")
    @ColDefine(width = 255)
    @ApiModelProperty(value = "描述")
    private String description;//描述
    
    @Column
    @Comment("标题")
    @ColDefine(width = 255)
    @ApiModelProperty(value = "标题")
    private String title;//标题

    @Column
    @Comment("推送平台 0全部 1 iOS 2 Android")
    @ColDefine(width = 1)
    @ApiModelProperty(value = "推送平台 0全部 1 iOS 2 Android")
    private Integer platform;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("推送文章链接")
    @ApiModelProperty(value = "推送文章链接")
    private String articleUrl;

    @Column(hump = true)
    @Comment("推送文章id")
    @ApiModelProperty(value = "推送文章id")
    private Long articleId;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("推送结果")
    @ApiModelProperty(value = "推送结果")
    private String responseResult;

    @Column(hump = true)
    @ColDefine(width = 1)
    @Comment("推送状态：1成功，0失败")
    @Default("true")
    @ApiModelProperty(value = "推送状态：1成功，0失败")
    private boolean status;

    private String sysCode;
    private String type;

    public static class Constant{
        public static final String DEL_FLAG="del_flag";
        public static final String CREATE_AT="create_at";
        public static final String TITLE="title";
        public static final String DESCRIPTION="description";
    }
}
