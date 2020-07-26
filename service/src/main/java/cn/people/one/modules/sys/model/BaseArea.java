package cn.people.one.modules.sys.model;


import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Prev;

import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;


@Data
public class BaseArea extends BaseEntity
{
    private static final long serialVersionUID = -5392990889062087197L;

    @Column()
    @Comment("国标城市编码人")
    @ColDefine(width=40)
    private String code;// 国标城市编码

    @Column()
    @Comment("省")
    @ColDefine(width=40)
    private String province;// 省

    @Column()
    @Comment("市")
    @ColDefine(width=40)
    private String city;// 市

    @Column()
    @Comment("区")
    @ColDefine(width=40)
    private String district;//

    @Column()
    @Comment("父区域id")
    @ColDefine(width=40)
    private String parent;

    @Column()
    @Comment("级别 0 国家 1省 2市 3 区县")
    @ColDefine(width=40)
    private String level; // 级别 0 国家 1省 2市 3 区县

    @Column()
    @Comment("省的短名称")
    @ColDefine(width=40)
    private String province_short;// 省的短名称

    @Column()
    @Comment("省的简称")
    @ColDefine(width=40)
    private String province_jiancheng;// 省的简称

    @Column()
    @Comment("城市拼音")
    @ColDefine(width=40)
    private String city_pinyin;// 城市

    public String getTitle()
    {
        return StringUtils.isNotEmpty(
            this.getProvince()) ? this.getProvince() : StringUtils.isNotEmpty(
                this.getCity()) ? this.getCity() : this.getDistrict();
    }

    public String toString()
    {
        return "BaseArea [ code=" + code + ", province=" + province + ", city=" + city
               + ", district=" + district + ", parent=" + parent + ", level=" + level
               + ", province_short=" + province_short + ", province_jiancheng="
               + province_jiancheng + ", city_pinyin=" + city_pinyin + "]";
    }

}
