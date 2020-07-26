package cn.people.one.appapi.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by sunday on 2018/11/28.
 */
@Data
public class LifeServiceVO {

    private List<LifeItemVO> focus;
    private List<LifeItemVO> lists;
}
