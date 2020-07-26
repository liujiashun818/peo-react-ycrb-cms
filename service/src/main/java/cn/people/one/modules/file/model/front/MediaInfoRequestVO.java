package cn.people.one.modules.file.model.front;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by sunday on 2018/11/1.
 */
@Data
@ApiModel(value = "文件上传结果查询入参", description = "文件上传结果查询入参对象")
public class MediaInfoRequestVO {

    @ApiModelProperty(value = "文件url 多个文件以逗号分隔")
    private String fileUrls;
}
