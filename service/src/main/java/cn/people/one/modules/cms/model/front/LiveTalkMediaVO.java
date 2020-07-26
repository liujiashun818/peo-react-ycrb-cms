package cn.people.one.modules.cms.model.front;

import cn.people.one.core.util.time.DateHelper;
import cn.people.one.modules.file.FileFormateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author YX
 * @date 2019-06-10
 * @comment
 */
@Data
public class LiveTalkMediaVO implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "索引（图集的排序，预留将来可以插入在正文的不同位置）")
    private String index;//索引（图集的排序，预留将来可以插入在正文的不同位置）
    @ApiModelProperty(value = "名称")
    private String title;//名称
    @ApiModelProperty(value = "类型（图集、视频、音频）")
    private String type;//类型（图集、视频、音频）
    @ApiModelProperty(value = "时长（仅音频、视频有）")
    private String times;//时长（仅音频、视频有）
    @ApiModelProperty(value = "（音频视频是封面）")
    private String coverImg;//（音频视频是封面）
    @ApiModelProperty(value = "图集中的真实图片")
    private String realImage;//图集中的真实图片
    @ApiModelProperty(value = "描述")
    private String description;//描述
    @ApiModelProperty(value = "音频/视频地址")
    private String url;//音频/视频地址
    @ApiModelProperty(value = "文件大小")
    private Long size;
    public String getCoverImg() {
        return FileFormateUtil.formatFileSrcToWeb(coverImg);
    }

    public String getTimes() {
        return DateHelper.secToTime(times);
    }
}
