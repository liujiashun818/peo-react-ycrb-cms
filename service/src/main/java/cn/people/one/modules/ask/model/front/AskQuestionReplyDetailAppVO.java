/**
 *   
 * 
 * @Title: AskQuestionReplyDetailVO.java 
 * @Package cn.people.one.modules.ask.model.front 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Administrator
 * @date 2019年2月20日 下午3:00:14 
 * @version V1.0   
 */
package cn.people.one.modules.ask.model.front;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


/**
 *  
 * 
 * @ClassName: AskQuestionReplyDetailVO 
 * @Description: 问政详情信息
 * @author Administrator
 * @date 2019年2月20日 下午3:00:14    
 */
@Data
public class AskQuestionReplyDetailAppVO
{
    Long questionId;// 问政id

    String repUserName;//

    Integer followNum;// 关注数

    Integer likeNum;// 关注数

    String viewType;//问政类型

    String newsLink;//新闻链接

    String prefixId;//

    Integer domainId;// 留言领域id

    Integer typeId;// 留言类型id

    String originalTitle;// 原始标题

    String title;// 移动端标题

    String questionContent;// 留言正文

    String userName;// 真实留言姓名

    String organization;// 办理机构

    String replyContent;// 回复正文
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date replyTime;// 回复时间

    Integer status;// 留言状态

    Integer commentsNum;// 真实评论数

    Integer falsecommentsNum;// 评论显示数

    Integer zanNum;// 真实点赞数

    Integer falseZanNum;// 显示点赞数

    String headImage;// 头图

    String shareLogo;// 分享图标

    String attachment;// 提问附件

    String realUserName;// 提问人真实姓名

    String userPhone;// 提问人联系手机号

    String sysCode;// 模块编码

    String fieldAndType;// 领域分类标签

}
