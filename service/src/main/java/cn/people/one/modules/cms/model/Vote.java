package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by lml on 17-3-15.
 */
@Table("cms_vote")
@Data
public class Vote extends BaseEntity {

	@ApiModelProperty(value = "文章id")
    @Column(hump=true)
    private Long articleId;

	@ApiModelProperty(value = "标题")
    @Column
    @ColDefine(width = 500)
    @Comment("标题")
    private String title;

	@ApiModelProperty(value = "类型 单选 多选")
    @Column
    @Comment("类型 单选 多选")
    @ColDefine(type = ColType.INT)
    private Integer type;

	@ApiModelProperty(value = "结束时间")
    @Column
    @Comment("结束时间")
    @ColDefine(type = ColType.DATETIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    @Many(field = "voteId")
    List<VoteOption> options;

    @ApiModelProperty(value = "是否显示结果")
    @Column(hump = true)
    @Comment("是否显示结果")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean isShowResult;

    public Vote(){
        if(this.getDelFlag() == null){
            this.setDelFlag(Vote.STATUS_ONLINE);
        }
    }

}
