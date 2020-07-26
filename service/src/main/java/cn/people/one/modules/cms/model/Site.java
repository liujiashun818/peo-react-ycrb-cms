package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

/**
 * Created by lml on 2016/12/22.
 */
@Table("cms_site")
@Data
public class Site extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 模板路径
     */
    public static final String TPL_BASE = "/WEB-INF/views/modules/cms/front/themes";
    /**
     * 获取默认站点ID
     */
    public static Long defaultSiteId() {
        return 1L;
    }
    /**
     * 获取当前编辑的站点编号
     */
   // @Cacheable(value = "CACHE_SITE_CURRENTID")
    public static Long getCurrentSiteId() {
        return defaultSiteId();
    }
    /**
     * 判断是否为默认（主站）站点
     */
    public static boolean isDefault(String id) {
        return id != null && id.equals(defaultSiteId());
    }
    @Column
    @Comment("站点名称")
    private String name; // 站点名称
    @Column
    @Comment("站点标题")
    private String title; // 站点标题
    @Column
    @Comment("站点logo")
    private String logo; // 站点logo
    @Column
    @Comment("描述")
    private String description;// 描述，填写有助于搜索引擎优化

    @Column
    @Comment("版权信息")
    private String copyright;// 版权信息
    @Column(hump=true)
    @Comment("自定义首页视图")
    private String customIndexView;// 自定义首页视图文件
    @Column
    @Comment("主题")
    private String theme;//主题
    /**
     * 获得模板方案路径。如：/WEB-INF/views/modules/cms/front/themes/people
     *
     * @return
     */
    public String getSolutionPath() {
        return TPL_BASE + "/" + getTheme();
    }

    public Site(Long id) {
        setId(id);
    }

    public Site(){

    }

}
