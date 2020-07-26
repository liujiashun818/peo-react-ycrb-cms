package cn.people.one.modules.cms.model.front;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by lml on 2016/12/22.
 */

@Data
public class ArticleVO {

    @ApiModelProperty(value = "文章id")
    private Long articleId;

    @ApiModelProperty(value = "查询起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;//查询起始时间

    @ApiModelProperty(value = "查询截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;//查询截止时间

    @ApiModelProperty(value = "权重过期时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weightDate;//权重过期时间

    @ApiModelProperty(value = "查询删除文章起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteBeginTime;//查询起始时间

    @ApiModelProperty(value = "查询删除文章截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteEndTime;//查询截止时间

    @ApiModelProperty(value = "栏目ID")
    private Long categoryId;

    @ApiModelProperty(value = "区块 1. 头图 2.列表 3.待选 同一个栏目下可能有不同文章区块，如客户端的头图区和列表区")
    private Integer block;

    @ApiModelProperty(value = "状态标记(0：正常；1：下线；2：审核；3：删除；4:审核未通过)")
    private Integer delFlag;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "关键字")
    private String keywords;

    @ApiModelProperty(value = "文章类型 普通新闻，图片，音频，视频，快讯，直播，专题，公益")
    private String type;//文章类型

    @ApiModelProperty(value = "是否在专题中")
    private Boolean inSubject;

    @ApiModelProperty(value = "系统编码 引用文章可能来自其他系统模块，系统编码用于区分原文所在系统模块")
    private String sysCode;

    @ApiModelProperty(value = "作者")
    private String authors;

    @ApiModelProperty(value = "扩展字段名称")
    private String fieldName;//扩展字段名称

    @ApiModelProperty(value = "扩展字段值")
    private String fieldValue;//扩展字段值

    @ApiModelProperty(value = "当前页")
    private Integer pageNumber=1;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;

    @ApiModelProperty(value = "降序检索字段")
    private String desc; //降序检索字段

    @ApiModelProperty(value = "升序检索字段")
    private String asc;//升序检索字段

    @ApiModelProperty(value = "帮的状态 1募集中  2募集结束  3捐助反馈")
    private Integer helpStatus;

    @ApiModelProperty(value = "帮的声明")
    private String helpState;

    @ApiModelProperty(value = "定时发布起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fixedBeginTime;//定时发布起始时间

    @ApiModelProperty(value = "定时发布截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fixedEndTime;//定时发布截止时间

    @ApiModelProperty(value = "定时发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date fixedPubTime;

    @ApiModelProperty(value = "定时发布查询标识")
    private Integer fixedPubFlag;

    @ApiModelProperty(value = "分享链接")
    private String ShareUrl;

    @ApiModelProperty(value = "外部链接")
    private String link;

    @ApiModelProperty(value = "文章正文")
    private String content;
    public ArticleVO() {
        inSubject = false;
    }

}
