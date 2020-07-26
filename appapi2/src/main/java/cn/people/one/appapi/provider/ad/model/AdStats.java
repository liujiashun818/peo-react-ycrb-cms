package cn.people.one.appapi.provider.ad.model;

import lombok.Data;

@Data
public class AdStats {
    private String ctime;
    private String sp;
    private String network_state;
    private String visit_id;
    private String visit_start_time;
    private String latitude;
    private String longitude;
    private String MCC;
    private String MNC;
    private String isoCC;
    private String user_name;
    private String user_type;
    private String user_id;

    private String event_name;// 类型区分，AD
    private String ad_id;// 广告id
    private String ad_title;// 广告标题
    private String operate_time;// 操作时间，13位时间戳
    private String state;// 统计方式，VIEW：展现量，CLICK点击量
    private String advert_position;
}
