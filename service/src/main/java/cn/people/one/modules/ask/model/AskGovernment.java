package cn.people.one.modules.ask.model;

import cn.people.one.modules.ask.model.front.AskGovernmentVO;
import io.swagger.annotations.ApiModelProperty;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;

import java.util.List;

@Table("ask_government")
@Data
public class AskGovernment extends BaseEntity{
    @Column
    @ColDefine(width = 255)
    @Comment("政府部门名称")
    @ApiModelProperty(value = "政府部门名称")
    private String name;
    @Column(hump=true)
    @ColDefine(width = 255)
    @Comment("简称")
    @ApiModelProperty(value = "简称")
    private String shortName;
    @Column
    @ColDefine(width = 3,type=ColType.INT)
    @Comment("排序")
    @ApiModelProperty(value = "排序")
    private Byte sort;
    @Column(hump=true)
    @ColDefine(width = 1,type=ColType.INT)
    @Comment("是否有下一级部门：0无、1有，默认为0")
    @ApiModelProperty(value = "是否有下一级部门：0无、1有，默认为0")
    private Boolean hasChild;
    @Column(hump=true)
    @ColDefine(width = 1,type=ColType.INT)
    @Comment("首字母简称，如北京BJ")
    @ApiModelProperty(value = "首字母简称，如北京BJ")
    private String firstChar;
    @Column(hump=true)
    @ColDefine(width = 10,type=ColType.INT)
    @Comment("留言总数")
    @ApiModelProperty(value = "留言总数")
    private int questionNum;

    @Column(hump=true)
    @ColDefine(width = 10,type=ColType.INT)
    @Comment("回复总数")
    @ApiModelProperty(value = "回复总数")
    private int replyNum;
    @Column(hump=true)
    @ColDefine(width = 10)
    @Comment("行政区划编码")
    @ApiModelProperty(value = "行政区划编码")
    private String areaCode;
    @Column(hump=true)
    @ColDefine(width = 10)
    @Comment("如果不是地方政府，则表示该部门的上一级部门id；0表示最高级部门的上一级部门id")
    @ApiModelProperty(value = "如果不是地方政府，则表示该部门的上一级部门id；0表示最高级部门的上一级部门id")
    protected Integer parentID;

    @Column
    @ColDefine(width = 6)
    @Comment("如果是地方政府，则表示地方政府id，与地方部提供的地方政府fid相同")
    @ApiModelProperty(value = "如果是地方政府，则表示地方政府id，与地方部提供的地方政府fid相同")
    protected Long fid; //如果是地方政府，则表示地方政府id，与地方部提供的地方政府fid相同
    @Column
    @ColDefine(width = 6)
    @Comment("如果是地方政府，则表示该地方政府上一级地方政府id，与地方部提供的地方政府fup相同")
    @ApiModelProperty(value = "如果是地方政府，则表示该地方政府上一级地方政府id，与地方部提供的地方政府fup相同")
    protected Long fup; //地方政府父ID
    @Column(hump=true)
    @ColDefine(width = 6)
    @Comment("如果是地方政府，则表示地方政府id，与地方部提供的地方政府fid相同")
    @ApiModelProperty(value = "如果是地方政府，则表示地方政府id，与地方部提供的地方政府fid相同")
    protected boolean isLocalGov;
    private List<AskGovernment> child;
}