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


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;


/**
 *  
 * 
 * @ClassName: AskQuestionReplyDetailVO 
 * @Description: 问政详情信息
 * @author Administrator
 * @date 2019年2月20日 下午3:00:14    
 */
@Data
public class AskQuestionReplyDetailVO
{
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

    Long tid;

    private Integer publishStatus;//上线状态

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date questionTime;

    Integer orderID;

    String udid;//手机标识符
}
