package cn.people.one.modules.cms.model.front;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class RevelationsVO {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;//创建时间

    private String delFlag;//删除标记，0是上线状态

    private String remarks;//备注

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;//更新时间

    private String contactInfo;//联系信息

    private String content;//内容

    private String medias;//图片

    private String name;//姓名

    private String title;//标题
    
    private Integer pageNumber;
    private Integer pageSize;
}
