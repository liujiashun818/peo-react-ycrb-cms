package cn.people.one.modules.ask.model;

import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.search.model.AskSuggestData;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;

@Table("ask_question_reply")
@Data
public class AskQuestionReply  extends BaseEntity{
    private static final long serialVersionUID = -2535573032310329869L;
    
    @Column(hump=true)
    @ColDefine(width = 10)
    @Comment("对应Ask_Government表中的id")
    @ApiModelProperty(value = "对应Ask_Government表中的id")
    private Long governmentId;
    
    @Column(hump=true)
    @ColDefine(width = 10)
    @Comment("对应Ask_Domain表中的id")
    @ApiModelProperty(value = "对应Ask_Domain表中的id")
    private Integer domainId;
    
    @Column(hump=true)
    @ColDefine(width = 10)
    @Comment("对应Ask_Type表中的id")
    @ApiModelProperty(value = "对应Ask_Type表中的id")
    private Integer typeId;
    
    @Column
    @ColDefine(width = 300)
    @Comment("原始标题，原始标题不可修改")
    @ApiModelProperty(value = "原始标题，原始标题不可修改")
    private String originalTitle;  //原始标题，原始标题不可修改
    
    @Column
    @ColDefine(width = 300)
    @Comment("留言标题")
    @ApiModelProperty(value = "留言标题")
    private String title;	//移动端标题可以修改

    @Column(hump=true)
    @ColDefine(width = 1)
    @Comment("是否推送到文章里面 0 未推送 1 已经推送")
    @ApiModelProperty(value = "是否推送到文章里面 0 未推送 1 已经推送")
    private Integer isPush;
    
    @Column(hump=true)
    @ColDefine(width = 4000)
    @Comment("留言正文")
    @ApiModelProperty(value = "留言正文")
    private String questionContent;
    
    @Column
    @ColDefine(width = 1)
    @Comment("留言状态：0未审核、1已审核、2办理中、3已回复,4审核未通过，默认为0")
    @ApiModelProperty(value = "留言状态：0未审核、1已审核、2办理中、3已回复,4审核未通过，默认为0")
    private Integer status;
    
    @Column
    @ColDefine(width = 300)
    @Comment("附件")
    @ApiModelProperty(value = "附件")
    private String attachment;
    
    @Column
    @ColDefine(width = 255)
    @Comment("文件类型")
    @ApiModelProperty(value = "文件类型")
    private String fileType;
    
    @Column
    @ColDefine(width = 255)
    @Comment("文件名")
    @ApiModelProperty(value = "文件名")
    private String fileName;
    
    @Column(hump=true)
    @ColDefine(type = ColType.DATETIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("留言时间")
    @ApiModelProperty(value = "留言时间")
    private Date questionTime;
    
    @Column(hump=true)
    @ColDefine(width = 1000)
    @Comment("如果是登陆用户，则表示用户id；否则为空")
    @ApiModelProperty(value = "如果是登陆用户，则表示用户id；否则为空")
    private String userId;

    @Column(hump=true)
    @ColDefine(width = 255)
    @Comment("用户昵称，（原来意思是真实姓名，现在改作用户昵称，另有别的字段存储真实姓名）")
    @ApiModelProperty(value = "用户昵称，（原来意思是真实姓名，现在改作用户昵称，另有别的字段存储真实姓名）")
    private String userName;

    @Column(hump=true)
    @ColDefine(width = 255)
    @Comment("留言时输入的联系电话")
    @ApiModelProperty(value = "留言时输入的联系电话")
    private String userPhone;
    
    @Column(hump=true)
    @ColDefine(width = 255)
    @Comment("留言时设备的ip地址")
    @ApiModelProperty(value = "留言时设备的ip地址")
    private String userIp;
    
    @Column
    @ColDefine(width = 255)
    @Comment("手机唯一标识码")
    @ApiModelProperty(value = "手机唯一标识码")
    private String udid;

    @Column
    @ColDefine(width = 10)
    @Comment("如果是对地方政府的提问，则表示提问id，与地方部提供的数据的tid相同；否则为空")
    @ApiModelProperty(value = "如果是对地方政府的提问，则表示提问id，与地方部提供的数据的tid相同；否则为空")
    private Long tid;

    @Column(hump = true)
    @ColDefine(type = ColType.DATETIME)
    @Comment("回复时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "回复时间")
    private Date replyTime;

    @Column
    @ColDefine(width = 100)
    @Comment("回复单位")
    @ApiModelProperty(value = "回复单位")
    private String organization;
    
    @Column
    @ColDefine(width = 10)
    @Comment("如果是地方政府的回复，则表示回复id，与地方部提供的数据的asid相同；否则为空")
    @ApiModelProperty(value = "如果是地方政府的回复，则表示回复id，与地方部提供的数据的asid相同；否则为空")
    private Integer asid;
   
    @Column(hump = true)
    @ColDefine(width = 10000)
    @Comment("回复正文")
    @ApiModelProperty(value = "回复正文")
    private String replyContent;
    
    @Column("Comments_Num")
    @ColDefine(width = 10)
    @Comment("真评论数")
    @ApiModelProperty(value = "真评论数")
    private Integer commentsNum;	//真评论数

    @Column("falseComments_Num")
    @ColDefine(width = 10)
    @Comment("假评论数")
    @ApiModelProperty(value = "假评论数")
    private Integer falsecommentsNum;	//假评论数
    
    @Column
    @ColDefine(width = 1)
    @Comment("是否显示真评论")
    @ApiModelProperty(value = "是否显示真评论")
    private Boolean showTrueComment; //是否显示真评论
    
    @Column("Order_ID")
    @ColDefine(width = 11)
    @Comment("排序")
    @ApiModelProperty(value = "排序")
    private Integer orderID;	//排序
    
    @Column("Publish_Status")
    @ColDefine(width = 3)
    @Comment("发布状态")
    @ApiModelProperty(value = "发布状态")
    private Integer publishStatus;	//发布状态
    
    @Column("Publish_Time")
    @Comment("发布时间")
    @ColDefine(type = ColType.DATETIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "发布时间")
    private Date publishTime;	//发布时间
    
    @Column("Focus_Pic")
    @Comment("焦点图")
    @ColDefine(width = 3)
    @ApiModelProperty(value = "焦点图")
    private Integer focusPic;	//焦点图
    
    @Column("Is_Top")
    @Comment("是否置顶")
    @ColDefine(width = 1)
    @ApiModelProperty(value = "是否置顶")
    private Boolean isTop; //是否置顶
    
    @Column("Zan_Num")
    @Comment("点赞数")
    @ColDefine(width = 10)
    @ApiModelProperty(value = "点赞数")
    private Integer zanNum; //点赞数
    
    @Column("false_zan_num")
    @Comment("点赞显示数")
    @ColDefine(width = 10)
    @ApiModelProperty(value = "点赞显示数")
    private Integer falseZanNum; //点赞显示数
    
    @Column("Cai_Num")
    @Comment("点踩数")
    @ColDefine(width = 10)
    @ApiModelProperty(value = "点踩数")
    private Integer caiNum;  //点踩数
    
    @Column("source")
    @Comment("来源：0网络、1短信、2客户端、3微博、4微信、5人民日报客户端")
    @ColDefine(width = 3)
    @ApiModelProperty(value = "来源：0网络、1短信、2客户端、3微博、4微信、5人民日报客户端")
    private Byte source;
    
    @Column("Is_Checked")
    @Comment("是否推送：0未推送、1已推送，默认为0")
    @ColDefine(width = 1)
    @ApiModelProperty(value = "是否推送：0未推送、1已推送，默认为0")
    private Boolean isChecked;
    
    @Column
    @Comment("设备型号，如LG-D729")
    @ColDefine(width = 100)
    @ApiModelProperty(value = "设备型号，如LG-D729")
    private String devicemodel;
    
    @Column
    @Comment("设备类型 1：ios 2：android 3：wp")
    @ColDefine(width = 3)
    @ApiModelProperty(value = "设备类型 1：ios 2：android 3：wp")
    private Byte devicetype;
    
    @Column
    @Comment("设备分辨率，采用宽X高格式")
    @ColDefine(width = 100)
    @ApiModelProperty(value = "设备分辨率，采用宽X高格式")
    private String devicesize;

    @Column
    @Comment("网络型号，将获取的网络类型字符串到服务器（如wifi，3g等）")
    @ColDefine(width = 100)
    @ApiModelProperty(value = "网络型号，将获取的网络类型字符串到服务器（如wifi，3g等）")
    private String nettype;
    
    @Column
    @Comment("系统版本号")
    @ColDefine(width = 100)
    @ApiModelProperty(value = "系统版本号")
    private String osversion;
    
    @Column
    @Comment("软件版本")
    @ColDefine(width = 100)
    @ApiModelProperty(value = "软件版本")
    private String version;
    
//    protected Integer creatorId;
//	protected Date createTime;
    
    @Column
    @Comment("软件版本")
    @ColDefine(width = 10)
    @ApiModelProperty(value = "软件版本")
	protected Integer updaterId;
    
    
    @Column
    @Comment("更新时间")
    @ColDefine(type = ColType.DATETIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
	protected Date updateTime;
	
    /* 无对应数据库字段：时间格式转换 Begin */
//	private String beginTimeFormat;
//	private String endTimeFormat;
//	private String content;  //转换用户推送页面获取内容
	/* 无对应数据库字段：时间格式转换 End */
    @ApiModelProperty(value = "领域名称")
	private String askDomainName;
    
    @ApiModelProperty(value = "类型明细")
	private String askTypeName;
    
    @ApiModelProperty(value = "政府部门名称")
	private String askGovernmentName;
	
	
	@Column(hump=true)
    @Comment("评价码")
    @ColDefine(width=100)
	@ApiModelProperty(value = "评价码")
	private String anonymousCode;
	
	@Column
    @Comment("第一次评价状态    0 第一次评价未审核    1 第一次评价审核通过    2第一次评价审核不通过    3 评价解释已回复")
    @ColDefine(width=4)
	@ApiModelProperty(value = "第一次评价状态    0 第一次评价未审核    1 第一次评价审核通过    2第一次评价审核不通过    3 评价解释已回复")
	private Byte status1;//第一次评价状态    0 第一次评价未审核    1 第一次评价审核通过    2第一次评价审核不通过    3 评价解释已回复
	
	@Column
    @Comment("0第二次评价未审核     1 第二次评价审核通过        2第二次评价审核不通过")
    @ColDefine(width=4)
	@ApiModelProperty(value = "0第二次评价未审核     1 第二次评价审核通过        2第二次评价审核不通过")
	private Byte status2;// 
	
	@Column(hump=true)
    @Comment("是否催办0 未催办 1已催办")
    @ColDefine(width=4)
	@ApiModelProperty(value = "是否催办0 未催办 1已催办")
	private Integer isPress;//是否催办过 0未催办 1已催办
	
	@Column(hump=true)
    @Comment("接口那边的问题码")
    @ColDefine(width=4)
	@ApiModelProperty(value = "接口那边的问题码")
	private String returnCode;//接口那边的问题码
	
//	private Integer fid;
	@Column("user_id2")
    @Comment("用户中心用户id")
    @ColDefine(width=20)
	@ApiModelProperty(value = "用户中心用户id")
	private Long userId2;
	
	@Column("real_user_name")
    @Comment("用户留言时候的真实姓名")
    @ColDefine(width=255)
	@ApiModelProperty(value = "用户留言时候的真实姓名")
	private String realUserName;//用户留言时候的真实姓名
	
	@Column(hump=true)
    @Comment("是否显示用户真实姓名 1:匿名用户  0:非匿名")
    @ColDefine(width=1)
	@ApiModelProperty(value = "是否显示用户真实姓名 1:匿名用户  0:非匿名")
	private Integer isUnknownUser;//是否显示用户真实姓名 1:匿名用户  0:非匿名
	
	@Column(hump=true)
    @Comment("分享页图标")
    @ColDefine(width=1000)
	@ApiModelProperty(value = "分享页图标")
	private String shareLogo;//分享页图标
	
    @Column(hump=true)
    @Comment("是否头条 0=否 1=是")
    @ColDefine(width=1)
    @ApiModelProperty(value = "是否头条 0=否 1=是")
	private Integer isHeadline;//是否头条 0=否 1=是
    
    @Column(hump=true)
    @Comment("头图")
    @ColDefine(width=3000)
    @ApiModelProperty(value = "头图")
	private String headImage;//头图

    @Column(hump=true)
    @Comment("关注数")
    @ColDefine(width=1)
    @ApiModelProperty(value = "关注数")
    private Integer followNum;//

    @Column(hump=true)
    @Comment("模块编码")
    @ColDefine(width=300)
    @ApiModelProperty(value = "模块编码")
    private String sysCode;//

    @Column(hump=true)
    @Comment("提问人头像")
    @ColDefine(width=3000)
    @ApiModelProperty(value = "提问人头像")
    private String userPhoto;//

    @Column(hump=true)
    @Comment("回复人头像")
    @ColDefine(width=3000)
    @ApiModelProperty(value = "回复人头像")
    private String organizationPhoto;//

    @ApiModelProperty(value = "文章ID")
    private Long articleId;

    @ApiModelProperty(value = "分享地址")
    private String shareUrl;

    @Column
    @ColDefine(width = 255)
    @Comment("推送ID")
    @ApiModelProperty(value = "推送ID")
    private String pushid;

    @ApiModelProperty(value = "领域分类标签")
    String fieldAndType;// 领域分类标签
    /**
     * 判断是否正常（即 可以显示到前端）
     * @return
     */
    public boolean isNormal() {
        if (this.getStatus() !=null && this.getStatus()>0 && this.getStatus()<4) {
            // 状态： 1已审核、2办理中、3已回复, (不要 0未审核、 4审核未通过)
            if(this.getPublishStatus() !=null && this.getPublishStatus()==1) {
                // 发布状态 1：表示 已发布
                //if(this.getIsHeadline() ==null || this.getIsHeadline()!=1) {
                    // 是否头条 不要头条
                    return true;
                //}
            }
        }
        return false;
    }
}
