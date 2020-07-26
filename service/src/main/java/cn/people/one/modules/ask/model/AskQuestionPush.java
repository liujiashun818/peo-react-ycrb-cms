package cn.people.one.modules.ask.model;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

@Table("ask_question_push")
@Data
public class AskQuestionPush extends BaseEntity{

    @Column
    @ColDefine(width = 10)
    @Comment("推送的问政ID")
    @ApiModelProperty(value = "推送的问政ID")
    private Long askId;

    @Column(hump=true)
    @ColDefine(width = 4000)
    @Comment("推送正文")
    @ApiModelProperty(value = "推送正文")

    private String pushContent;
    @Column
    @ColDefine(width = 300)
    @Comment("留言标题")
    @ApiModelProperty(value = "留言标题")
    private String title;

    @Column
    @ColDefine(width = 255)
    @Comment("手机唯一标识码")
    @ApiModelProperty(value = "手机唯一标识码")
    private String udid;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("推送结果")
    @ApiModelProperty(value = "推送结果")
    private String responseResult;

    @Column(hump = true)
    @ColDefine(width = 1)
    @Comment("推送状态：1成功，0失败")
    @Default("true")
    @ApiModelProperty(value = "推送状态：1成功，0失败")
    private boolean status;
}
