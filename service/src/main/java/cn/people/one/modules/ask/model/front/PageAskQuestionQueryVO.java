package cn.people.one.modules.ask.model.front;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 
* @ClassName: PageAskQuestionQueryVO 
* @Description: 问政页面查询对象
* @author Administrator
* @date 2019年2月21日 上午9:17:13 
*  
 */
@Data
public class PageAskQuestionQueryVO  implements Serializable{
	private Long govId;
	private String startTime;//2019-02-21 09:34:41 格式
	private String endTime;
	/**
	 * 0未审核、1已审核、2办理中、3已回复,4审核未通过，默认为0
	 */
	private String status;
	private String title;	//移动端标题
	private String questionContent;//问题内容
	private String replyContent;//回复内容
	private String organization;//回复机构
	List<Long> governmentIds;//下级机构id，包括下级的下级
    private Integer domainId;//领域id
    private Integer typeId;//类型id
    private String source;//来源
	private String [] ids;//id列表
	private Integer current=1;//页码
	private Integer pageSize=30;//每页数据数
}
