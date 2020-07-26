package cn.people.one.appapi.vo.newspaper;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * 接受报纸系统外层对象数据
 */
@Data
public class NewsInfoList {
	@Id
	private String id;
	private String baseId;
	private String type;  //报纸 paper
	private long timestamp;  //发布时间时间戳
	private String pubDate;  //发布日期
	private List<NewsInfo> newsInfoList;  //新闻集合
}
