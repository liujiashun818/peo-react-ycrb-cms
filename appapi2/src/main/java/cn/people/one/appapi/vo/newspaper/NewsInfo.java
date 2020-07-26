package cn.people.one.appapi.vo.newspaper;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 接收报纸系统列表元素数据
 */
@Data
public class NewsInfo {
	//数据中心必填字段
	private String baseId;  //库ID，对应项目编号、报纸编号等
	private String sort;  //入库后的分类，根据该字段区分不同的栏目

	//文章基本属性
	private String id;// 文章id
	private String type;  //”媒体类型”,  (新闻1；专题2 ；聚合3；音频4；视频5；图片6 ；日报7）
	private String categoryId;//栏目ID
	private String category;// 栏目名称
	private String image;// 文章封面图url
	private String cover;  //封面（头图、列表缩略图）
	private String introTitle;  //肩标题
	private String title;  //标题
	private String subTitle;  //副标题
	private String mobileTitle;  //移动端标题
	private String authors;  //作者A,作者B
	private String summary;  //摘要
	private String mobileSummary;  //移动端摘要
	private String content;  //正文
	private String docTime;  //文档时间
	private String pubSource;  //”来源”,发布来源
	private String keywords;   //关键词
	private String tags; // 标签

	private String createDate;// 创建日期
	private String updateTime;// 修改时间

	private long status;  //”状态”,（新增0，修改1,删除2）
	private String from;    //从哪抓来的（在线0、多听1……）
	private Boolean isOriginal; //是否独家 true\false
	private long orderId;    //排序字段

	//报纸属性
	private String pageNum;  //版号
	private String pageName;  //版名
	private String issue;  //期号,总期号
	private String pagePic;//版面图
	private String coords;//热区


	//其他属性
	private String masterId; //主体id”,网站id,由我们整理提供
	private String sensWords;  //敏感词
	private String copyrightSource;  //”版权来源”
	private String sourceId;  //原始id”,在线生成的id
	private String sourceUrl; //原始url

	//媒体属性
	private List<NewsInfoMedias> medias = Lists.newArrayList();
	/**
	 * 是否在同一行显示
	 */
	private Integer rowNum;
}
