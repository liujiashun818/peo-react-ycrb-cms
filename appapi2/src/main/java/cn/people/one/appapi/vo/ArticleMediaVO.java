package cn.people.one.appapi.vo;

import cn.people.one.core.util.time.DateHelper;
import cn.people.one.modules.file.FileFormateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by wilson on 2018-10-11.
 */
@Data
public class ArticleMediaVO {
	
	@ApiModelProperty(value = "appId")
    private Long id;
	
	@ApiModelProperty(value = "索引（图集的排序，预留将来可以插入在正文的不同位置）")
    private String index;//索引（图集的排序，预留将来可以插入在正文的不同位置）
	
	@ApiModelProperty(value = "名称")
    private String title;//名称
	
	@ApiModelProperty(value = "类型（图集、视频、音频）")
    private String type;//类型（图集、视频、音频）
	
	@ApiModelProperty(value = "时长（仅音频、视频有）")
    private String times;//时长（仅音频、视频有）
	
	@ApiModelProperty(value = "图片（音频视频是封面，图集就是图片）")
    private String image;//图片（音频视频是封面，图集就是图片）
	
	@ApiModelProperty(value = "描述")
    private String description;//描述
	
	@ApiModelProperty(value = "实像")
    private String realImage;
	
	@ApiModelProperty(value = "媒体资源（仅音频视频有，不同编码对应的地址")
    private List<MediaResourceVO> resources;//媒体资源（仅音频视频有，不同编码对应的地址）

    public String getImage() {
        return FileFormateUtil.formatFileSrcToWeb(image);
    }
    public String getTimes() {
        return DateHelper.secToTime(times);
    }
}
