package cn.people.one.modules.newspaper.model;


import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;

@Data
public class NewspaperXmlVO extends BaseEntity {

    private String paperCode;

    private String paperName;

    private String nsdate;

    private String nodeName;

    private String source;

    private String type;

    private String docTime;

    private String pageNum;

    private String pageName;

    private String pagePic;

    private String docCode;

    private String introtitle;

    private String mobileTitle;

    private String title;

    private String subTitle;

    private String author;

    private String picUrl;

    private String picText;

    private String content;

    private String coords;

    private Integer width;

    private Integer height;

    private Integer pageNumber;
    private Integer pageSize;
    //降序检索字段
    private String desc;
    //升序检索字段
    private String asc;

    private String startTime;

    private String endTime;

    private Integer delFlag;
}
