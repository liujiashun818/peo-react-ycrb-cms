package cn.people.one.appapi.vo.newspaper;

import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * 接收报纸系统媒体文件数据
 */
@Data
public class NewsInfoMedias {
	private String title;  //标题
	private long type;   //媒体类型（音频 audio；视频 video；图片image；）
	private String summary;  //摘要
	private String times;  //时长
	private long encodeType; //媒体类型
	private String sourceUrl;  //原始url
	private String attach;  //文件路径, 共享区内，相对路径
	//详情新增字段
	private String canview;//
	private String typeStr;//
	private String encodeTypeStr;//
	private String imgUrl;//

	/**
	 * 常量类
	 */
	public static class Constant{
		public static final Integer TYPE_VIDEO=5;
		public static final Integer TYPE_IMAGE=6;
		public static final Integer ENCODETYPE_VIDEO1=52;
		public static final Integer ENCODETYPE_VIEDO2=54;
		public static final Integer ENCODETYPE_IAMGE1=0;
		public static final Integer ENCODETYPE_IMAGE2=61;
		public static final String IMG="img";
		public static final String VIDEO="video";
		public static final String VEDIO_IOS="iphone_m3u8";
		public static final String VEDIO_ANDROID="android_c";
		public static final String CANVIEW_YES="yes";
		public static final String CANVIEW_NO="no";
	}

	public static NewsInfoMedias init(NewsInfoMedias newsInfoMedias){
		NewsInfoMedias newsInfoMediasNew= null;
		try {
			newsInfoMediasNew = new NewsInfoMedias();
			BeanUtils.copyProperties(newsInfoMediasNew,newsInfoMedias);
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return newsInfoMediasNew;
	}
}
