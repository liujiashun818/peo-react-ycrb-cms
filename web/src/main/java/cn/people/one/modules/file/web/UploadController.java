package cn.people.one.modules.file.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.core.util.regex.ImageChecker;
import cn.people.one.core.util.regex.MediaChecker;
import cn.people.one.modules.file.model.front.MediaInfoRequestVO;
import cn.people.one.modules.file.service.IUploadService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * User: 张新征
 * Date: 2017/2/13 14:54
 * Description:
 */
@Api(description = "文件上传模块")
@RestController
@RequestMapping("/api/upload")
@Slf4j
public class UploadController {

	@Value("${theone.file.types}")
	private String fileTypes;

	@Value("${theone.file.image.size}")
    private Long imageSize;

    @Value("${theone.file.media.size}")
    private Long mediaSize;

	@Autowired
	private IUploadService uploadService;

	@RequestMapping(value = "file", method = RequestMethod.POST)
	public Result upload(@RequestParam("file") MultipartFile file, @RequestParam(required = false) String keyword) {
		String fileName = file.getOriginalFilename();
		//文件类型检查
		String[] names = fileName.split("\\.");
		if (names.length <= 1) {
			return Result.error(-1, "文件格式不正确");
		}
		String fileType = names[names.length - 1];
		if (!Lists.newArrayList(fileTypes.split(",")).contains(fileType.toLowerCase())) {
			return Result.error(-1, "文件类型只支持" + fileTypes + "类型");
		}
		//图片大小检查
        if(ImageChecker.isImage(fileType)){
		    if(file.getSize() > imageSize){
		        return Result.error(-1, "上传图片大小不能超过"+imageSize/1024/1024+"M");
            }
        }
        //媒体资源大小检查
        if(MediaChecker.isMedia(fileType)){
            if(file.getSize() > mediaSize){
                return Result.error(-1, "上传资源大小不能超过"+mediaSize/1024/1024+"M");
            }
        }
		return Result.success("文件上传成功", uploadService.upload(fileType, file, keyword));
	}

	@RequestMapping(value = "files", method = RequestMethod.POST)
	public Result uploads(@RequestParam("file") MultipartFile[] files) {
		Map<MultipartFile, String> types = Maps.newHashMap();
		//检查所有上传文件的类型，如有一个不合格则都不上传
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].getOriginalFilename();
			//文件类型检查
			String[] names = fileName.split("\\.");
			if (names.length <= 1) {
				return Result.error(-1, fileName + "文件格式不正确");
			}
			String fileType = names[names.length - 1];
			if (!Lists.newArrayList(fileTypes.split(",")).contains(fileType)) {
				return Result.error(-1, fileName + "格式不正确，文件类型只支持" + fileTypes + "类型");
			}
            //媒体资源大小检查
            if(MediaChecker.isMedia(fileType)){
                if(files[i].getSize() > mediaSize){
                    return Result.error(-1, "上传资源大小不能超过"+mediaSize/1024/1024+"M");
                }
            }
			types.put(files[i], fileType);
		}
		//上传所有的文件
		List<Map<String, Object>> list = Lists.newArrayList();
		types.forEach((file, type) -> list.add(uploadService.upload(type, file, null)));
		return Result.success("文件上传成功", list);
	}

    /**
     * 查询上传状态
     * @param mediaInfoRequestVO
     * @return
     */
    @RequestMapping(value = "checkUploadStatus", method = RequestMethod.POST)
    public Result checkUploadStatus(@RequestBody MediaInfoRequestVO mediaInfoRequestVO) {
        String fileUrls = mediaInfoRequestVO.getFileUrls();
        if(Lang.isEmpty(fileUrls)){
            return Result.error("参数不能为空！！！");
        }
        return Result.success(uploadService.checkUploadStatus(fileUrls));
    }
}
