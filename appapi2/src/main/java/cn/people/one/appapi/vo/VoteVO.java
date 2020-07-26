package cn.people.one.appapi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by wilson on 2018-10-11.
 */
@Data
public class VoteVO {
	
	@ApiModelProperty(value = "id")
    private Long id;
	
	@ApiModelProperty(value = "标题")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanghai")
    @ApiModelProperty(value = "时间")
    private Date date;
    
    @ApiModelProperty(value = "类型")
    private Integer type;
    
    @ApiModelProperty(value = "是否显示")
    private Boolean isShowResult;
    
    private List<VoteOptionVO> options;
}
