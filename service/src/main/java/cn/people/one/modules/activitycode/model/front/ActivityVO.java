package cn.people.one.modules.activitycode.model.front;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Created by sunday on 2017/4/13.
 */
@Data
public class ActivityVO {

    private String appId;//项目编号
    private String title;//标题
    private String digest;//摘要
    private Integer type;//类别（机构1 个人2）
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private Date beginTime;//开始时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private Date endTime;//结束时间
    private Integer maxNumber;//计数上限（每个邀请码最大使用次数）
    private Integer codeNumber;//生成邀请码数量(对应每个机构生成的邀请码数量)
    private String prefix;//活动字母标识（作为邀请码的前缀，用于接口通过邀请码识别活动）
}
