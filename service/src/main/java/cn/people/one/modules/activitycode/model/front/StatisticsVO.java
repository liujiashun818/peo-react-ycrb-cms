package cn.people.one.modules.activitycode.model.front;

import lombok.Data;

/**
 * 邀请码统计页面使用
 * Created by sunday on 2017/4/13.
 */
@Data
public class StatisticsVO {

    private String code;//邀请码
    private String number;//邀请码个数
}
