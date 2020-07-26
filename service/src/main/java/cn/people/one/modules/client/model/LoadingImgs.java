package cn.people.one.modules.client.model;

import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.file.FileFormateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;

/**
 * Loading图
 * Created by sunday on 2017/4/11.
 */
@Table("mobile_loading_imgs")
@Data
@ApiModel(value = "开屏图反参对象", description = "开屏图反参对象")
public class LoadingImgs extends BaseEntity {

    @Column
    @ColDefine(width = 255)
    @Comment("名称")
    @ApiModelProperty(value = "名称")
    private String name;

    @Column(hump = true)
    @ColDefine(customType = "LONGTEXT", type = ColType.TEXT)
    @Comment("图片地址")
    @ApiModelProperty(value = "图片地址")
    private String imgUrl;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("链接类型")
    @ApiModelProperty(value = "链接类型")
    private String linkType;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("链接值")
    @ApiModelProperty(value = "链接值")
    private String linkValue;

    @Column
    @Comment("展示时长")
    @ApiModelProperty(value = "展示时长")
    private Integer showtimes;

    @Column
    @Comment("上下线状态")
    @ApiModelProperty(value = "上下线状态")
    private Integer status;

    @Column
    @ColDefine(width = 20)
    @ApiModelProperty(value = "分辨率高")
    @Comment("分辨率高")
    private String height;

    @Column
    @ColDefine(width = 20)
    @Comment("分辨率宽")
    @ApiModelProperty(value = "分辨率宽")
    private String width;

    @Column(hump = true)
    @ColDefine(width = 20)
    @Comment("系统编码")
    @ApiModelProperty(value = "系统编码")
    private String sysCode;

    @Column(hump = true)
    @Comment("文章ID")
    @ApiModelProperty(value = "文章ID")
    private Long articleId;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("文章标题")
    @ApiModelProperty(value = "文章标题")
    private String articleTitle;

    @Column
    @ColDefine(width = 20)
    @Comment("类型")
    @ApiModelProperty(value = "类型")
    private String type;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("开屏图类型")
    @ApiModelProperty(value = "开屏图类型 （无用）")
    private String loadingImgsType;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("视频地址")
    @ApiModelProperty(value = "视频地址")
    private String videoUrl;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("视频封面地址")
    @ApiModelProperty(value = "视频封面地址")
    private String videoCoverUrl;

    @ApiModelProperty(value = "文章类型")
    private String articleType;

    public String getImgUrl() {
        return FileFormateUtil.formatFileSrcToWeb(this.imgUrl);
    }
}