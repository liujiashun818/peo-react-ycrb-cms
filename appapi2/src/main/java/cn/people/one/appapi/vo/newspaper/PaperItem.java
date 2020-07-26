package cn.people.one.appapi.vo.newspaper;

import lombok.Data;

import java.util.List;

/**
 * 报纸文章信息(客户端展示)
 * @author YX
 * @date 2018/10/19
 * @comment
 */
@Data
public class PaperItem {
    private String points;//热区: "382,3;1041,3;1041,233;382,233",
    private String id;//文章id: "20181008_56011353",
    private String paperName;//报纸编码: "xzrb","cdrb"
    private String pjCode;//: "xzrbc_10_201705",
    private String sysCode="paper";//系统编码: "paper",
    private String articleId;//文章id，同ID: "20181008_56011353",
    private String newsTimestamp;//文章时间: "1538928000000",
    private String newsDatetime;//文章日期: "2018-10-08",
    private String title;//文章标题: "《百家讲坛》将播出特别节目《平“语”近人—习近平总书记用典》",
    private Integer rowNum;//图文类型则左图右文（标题），无图并且紧跟那条新闻也是无图，那就两条无图新闻放在一行: 1,
    private String viewType;//paper_1（报纸在闻列表，左边有人民日报），paper_2（报纸一行文字），paper_3（报纸图文），paper_4（报纸两行）,paper_5（视频）;eg:"paper_4",
    private String categoryId;//: "8",
    private String newsLink;//: "1|20181008_56011353"
    private String videoLink1;//视频链接1
    private String videoLink2;//视频链接2
    private List<String> image;//封面（头图、列表缩略图）

    public static class Constant{
        //报纸的浏览类型
        public static final String view_type1="paper_1";
        public static final String view_type2="paper_2";
        public static final String view_type3="paper_3";
        public static final String view_type4="paper_4";
        public static final String view_type5="paper_5";

    }
}
