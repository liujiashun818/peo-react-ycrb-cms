package cn.people.one.appapi.vo.newspaper;

import lombok.Data;

import java.util.List;

/**
 * 报纸版面信息(客户端展示)
 * @author YX
 * @date 2018/10/19
 * @comment
 */
@Data
public class PaperPage {
    private String pageName;//版名
    private String pageNum;//版号
    private String periodNum;//文档日期
    private String pagePic;//版面图
    private List<PaperItem> paperItemList;
}
