package cn.people.one.modules.newspaper.model;


import cn.people.one.core.util.text.StringUtils;
import cn.people.one.core.util.time.DateHelper;
import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Table("newspaper_xml")
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class NewspaperXml extends BaseEntity {

    @Column(hump=true)
    @Comment("报纸编码 sxrb")
    @ApiModelProperty(value = "报纸编码 sxrb")
    private String paperCode;

    @Column(hump=true)
    @Comment("报纸名称")
    @ApiModelProperty(value = "报纸名称")
    private String paperName;

    @Column(hump=true)
    @Comment("日期编码 20190126")
    @ApiModelProperty(value = "日期编码 20190126")
    @XmlElement(name = "NSDATE")
    private String nsdate;

    @Column(hump=true)
    @Comment("报纸编码 xml")
    @ApiModelProperty(value = "报纸编码 xml")
    @XmlElement(name = "NODENAME")
    private String nodeName;

    @Column(hump=true)
    @Comment("报纸名称 xml")
    @ApiModelProperty(value = "报纸名称 xml")
    @XmlElement(name = "SOURCE")
    private String source;

    @Column(hump=true)
    @Comment("媒体类型,  (新闻1；专题2 ；聚合3；音频4；视频5；图片6 ；日报7）")
    @ApiModelProperty(value = "媒体类型,  (新闻1；专题2 ；聚合3；音频4；视频5；图片6 ；日报7）")
    private String type;

    @Column(hump=true)
    @Comment("报纸日期 1551196800000")
    @ApiModelProperty(value = "报纸日期 1551196800000")
    private String docTime;

    @Column(hump=true)
    @Comment("版面编码")
    @ApiModelProperty(value = "版面编码")
    @XmlElement(name = "PAGENUM")
    private String pageNum;

    @Column(hump=true)
    @Comment("版面名称")
    @ApiModelProperty(value = "版面名称")
    @XmlElement(name = "PAGENAME")
    private String pageName;

    @Column(hump=true)
    @Comment("背景图片")
    @ApiModelProperty(value = "背景图片")
    private String pagePic;

    @Column(hump=true)
    @Comment("文章编码 yyyyMMdd0000 id")
    @ApiModelProperty(value = "文章编码 yyyyMMdd0000 id")
    private String docCode;

    @Column(hump=true)
    @Comment("肩标题")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(value = "肩标题")
    @XmlElement(name = "INTROTITLE")
    private String introtitle;

    @Column(hump=true)
    @Comment("肩标题1")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(value = "肩标题1")
    @XmlElement(name = "INTROTITLE1")
    private String introtitle1;

    @Column(hump=true)
    @Comment("肩标题2")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(value = "肩标题2")
    @XmlElement(name = "INTROTITLE2")
    private String introtitle2;

    @Column(hump=true)
    @Comment("手机标题")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(value = "手机标题")
    private String mobileTitle;

    @Column(hump=true)
    @Comment("标题")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(value = "标题")
    @XmlElement(name = "TITLE")
    private String title;

    @Column(hump=true)
    @Comment("副标题")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(value = "副标题")
    @XmlElement(name = "SUBTITLE")
    private String subTitle;

    @Column(hump=true)
    @Comment("副标题1")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(value = "副标题1")
    @XmlElement(name = "SUBTITLE1")
    private String subTitle1;

    @Column(hump=true)
    @Comment("副标题2")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(value = "副标题2")
    @XmlElement(name = "SUBTITLE2")
    private String subTitle2;

    @Column(hump=true)
    @Comment("责任编辑")
    @ApiModelProperty(value = "责任编辑")
    @XmlElement(name = "AUTHOR")
    private String author;

    @Column(hump=true)
    @Comment("图片地址 分号分隔 正文用")
    @ApiModelProperty(value = "图片地址 分号分隔 正文用")
    @XmlElement(name = "PICURL")
    private String picUrl;

    @Column(hump=true)
    @Comment("图片说明 分号分隔 正文用")
    @ApiModelProperty(value = "图片说明 分号分隔 正文用")
    @XmlElement(name = "PICTEXT")
    private String picText;

    @Column(hump=true)
    @Comment("正文")
    @ApiModelProperty(value = "正文")
    @XmlElement(name = "CONTENT")
    private String content;

    @Column(hump=true)
    @Comment("热区坐标")
    @ApiModelProperty(value = "热区坐标")
    @XmlElement(name = "ZB")
    private String coords;

    @ApiModelProperty(value = "原图宽度")
    @XmlElement(name = "width")
    private Integer width;

    @ApiModelProperty(value = "原图高度")
    @XmlElement(name = "height")
    private Integer height;

    private List<Attachment> attachmentList;
    private String updateAtStr;

    private String docTimeStr;

    public String getDocTimeStr() {
        if(StringUtils.isNotBlank(this.docTime)){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            long lt=Long.valueOf(this.docTime);
            Date date = new Date(lt);
            return simpleDateFormat.format(date);
        }
       return docTimeStr;
    }

    public String getUpdateAtStr() {
        if(this.getUpdateAt()!=null){
            return DateHelper.getFormatByLong(DateHelper.DEFAULT_TIME_FORMAT,this.getUpdateAt());
        }
        return updateAtStr;
    }
}
