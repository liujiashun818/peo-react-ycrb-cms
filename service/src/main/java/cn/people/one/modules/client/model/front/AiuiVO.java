package cn.people.one.modules.client.model.front;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by sunday on 2018/11/2.
 */
@Data
@ApiModel(value = "语音反参对象", description = "语音反参对象")
public class AiuiVO {
    @ApiModelProperty(value = "对话：Hi，我是小晋，我可以和您聊新闻，还可以帮您查天气，您想了解什么？")
    private String dialogue;//对话
    @ApiModelProperty(value = "通用提示语：[我想看今日新闻,我想查天气,我想看本月新闻,我要读报]")
    private String[] cueWords;//通用提示语
}
