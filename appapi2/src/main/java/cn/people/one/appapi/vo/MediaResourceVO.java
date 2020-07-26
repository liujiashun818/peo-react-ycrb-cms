package cn.people.one.appapi.vo;

import cn.people.one.modules.file.FileFormateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wilson on 2018-10-10.
 */
@Data
public class MediaResourceVO {
	
	@ApiModelProperty(value = "url")
    private String url;
	
	@ApiModelProperty(value = "编码类型")
    private String enctype;
	
	@ApiModelProperty(value = "大小")
    private Long size;

    public String getUrl() {
        return FileFormateUtil.formatFileSrcToWeb(url);
    }
}
