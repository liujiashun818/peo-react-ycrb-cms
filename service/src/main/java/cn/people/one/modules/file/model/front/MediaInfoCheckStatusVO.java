package cn.people.one.modules.file.model.front;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by sunday on 2018/11/1.
 */
@Data
@ApiModel(value = "文件上传结果查询返参", description = "文件上传结果查询返参对象")
public class MediaInfoCheckStatusVO {

    @ApiModelProperty(value = "查询结果，全部存在时true")
    private boolean status;
    @ApiModelProperty(value = "已存在的文件url,")
    private List<String> hdUrlList;
}
