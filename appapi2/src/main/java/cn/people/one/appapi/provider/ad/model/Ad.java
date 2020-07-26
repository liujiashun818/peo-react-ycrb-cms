package cn.people.one.appapi.provider.ad.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Created by wilson on 2018-10-26.
 */
@Data
public class Ad {

    private Long id;
    private String name;
    private String title;

    @JSONField(name = "channael")
    private Long channel;

    @JSONField(name = "view_type")
    private Integer viewType;

    @JSONField(name = "start_time")
    private Long startTime;

    @JSONField(name = "end_time")
    private Long endTime;

    private Long showtimes;

    @JSONField(name = "message_type")
    private Integer messageType;

    @JSONField(name = "message_order")
    private Integer messageOrder;

    @JSONField(name = "click_type")
    private Integer clickType;

    @JSONField(name = "click_data")
    private String clickData;

    private Integer flag;

    private List<AdImage> images;

}
