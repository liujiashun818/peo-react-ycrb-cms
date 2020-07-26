package cn.people.one.modules.activitycode.model.front;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.nutz.dao.entity.annotation.Comment;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sunday on 2017/4/13.
 */
@Data
public class CodeVo implements Serializable{
    private static final long serialVersionUID = -4184853187150808652L;

        private String title;//活动名称
        private String digest;//摘要
        private String typeName;//类别名称（机构名称 用户名称）
        private String code;//邀请码
        @Comment("创建时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
        private Date createTime;
        @Comment("开始时间")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
        private Date beginTime;
        @Comment("结束时间")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
        private Date endTime;
        @Comment("计数上限")
        private Integer maxNumber;
        @Comment("激活次数")
        private Integer activeTimes;
        @Comment("是否有效")
        private boolean isValid;

    }
