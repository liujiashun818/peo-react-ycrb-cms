package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.cms.model.front.ArticleMediaVO;
import cn.people.one.modules.cms.model.front.MediaResourceVO;
import cn.people.one.modules.cms.model.type.ViewType;
import cn.people.one.modules.file.FileFormateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * Created by lml on 17-3-3.
 */
@Table("cms_subject")
@Data
@NoArgsConstructor
public class Subject extends BaseEntity {
    /**
     * 专题 parentId=0(区块 parentId=专题ID)
     */

    @Column(hump = true)
    @Comment("父级编号")
    @ColDefine(width = 10,type = ColType.INT)
    private Long parentId;

    @Column
    @ColDefine(width = 500,type = ColType.VARCHAR, notNull = true)
    @Comment("标题")
    private String title;

    @Column(hump = true)
    @ColDefine(width = 20,type = ColType.VARCHAR/*, notNull = true*/)
    @Comment("展示类型")
    private String viewType;

    @Column
    @ColDefine(width = 500,type = ColType.VARCHAR)
    @Comment("图片")
    private String image;

    @Column(hump = true)
    @ColDefine(type = ColType.BOOLEAN)
    @Comment("是否显示标题")
    private Boolean showTitle;

    /**
     * 类似category的model，都是留作将来可以通过设置不同的值来限定该栏目或者该专题或者该专题区块下只能添加某种类型的文章
     */
    @Column
    @ColDefine(width = 100,type = ColType.VARCHAR)
    @Comment("模型")
    private String model;//模型

    @Column
    @ColDefine(width = 500,type = ColType.VARCHAR)
    @Comment("简介")
    private String description;//简介

    /**
     * 区块需要设置权重
     */
    @Column
    @ColDefine(width = 10,type = ColType.INT)
    @Comment("权重")
    private Integer weight;

    @Column
    @ColDefine(width = 500,type = ColType.VARCHAR)
    @Comment("类型" )
    private String type;

    @Column(hump = true)
    @ColDefine(customType = "LONGTEXT", type = ColType.TEXT)
    @Comment("通栏使用的")
    private String bannerUrl;
    
    @ApiModelProperty(value = "专题页样式 1.普通  2.时间轴 ")
	@Column
	@Comment("专题页样式类型 1.普通  2.时间轴 ")
	private Integer pageType;
    
    @ApiModelProperty(value = "顶部显示  1.图片 2.视频 ")
	@Column
	@Comment("顶部显示  1.图片 2.视频")
	private Integer showTop;
    
	@ApiModelProperty(value = "媒体信息表id")
	private List<Long> mediaIds;//媒体信息表id
	
    @Override
    public void init(){
        if(parentId == null){
            parentId = 0L;
        }
        if(this.weight == null && this.getParentId()!=0){
            this.weight =30;//如果是区块，默认权重为30
        }
        if(this.getDelFlag() == null){
            this.setDelFlag(Subject.STATUS_ONLINE);
        }
        if(viewType==null){
            viewType = ViewType.NORMAL.value();
        }
    }

    public static String ID = "id";
    public static String WEIGHT = "weight";
    private List<Subject> blocks;
    private List<Article> articles;
    private Long categoryId;

    public String getImage() {
        return FileFormateUtil.formatFileSrcToWeb(this.image);
    }
}
