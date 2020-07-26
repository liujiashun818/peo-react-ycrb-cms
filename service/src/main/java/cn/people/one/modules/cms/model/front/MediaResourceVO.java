package cn.people.one.modules.cms.model.front;

import cn.people.one.modules.file.FileFormateUtil;
import lombok.Data;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * User: 张新征
 * Date: 2017/3/10 16:58
 * Description:
 */
@Data
public class MediaResourceVO implements Serializable{
	
	@ApiModelProperty(value = "//url")
    private String url;
	
	@ApiModelProperty(value = "编码类型")
    private String enctype;
	
	@ApiModelProperty(value = "文件大小")
    private Long size;
	
    public String getUrl() {
        return FileFormateUtil.formatFileSrcToWeb(url);
    }
}
