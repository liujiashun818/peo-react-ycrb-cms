package cn.people.one.appapi.controller;

import java.util.List;
import java.util.Map;

import cn.people.one.core.base.api.Result;
import cn.people.one.core.util.regex.MediaChecker;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.core.util.regex.ImageChecker;
import cn.people.one.modules.file.model.front.MediaInfoCheckStatusVO;
import cn.people.one.modules.file.model.front.MediaInfoRequestVO;
import cn.people.one.modules.file.service.IUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;


@Api(value = "app文件上传模块", tags = {"app文件上传模块"})
@RestController
@RequestMapping("/api/upload")
@Slf4j
public class AppUploadController {

	@Value("${theone.file.types}")
	private String fileTypes;

	@Value("${theone.file.image.size}")
    private Long imageSize;

	@Value("${theone.file.media.size}")
	private Long mediaSize;
	@Autowired
	private IUploadService uploadService;
/**
 返回fileUrl<BR>
 {
    "code": 0,
    "msg": "文件上传成功",
    "data": {
        "fileUrl": "http://rmrbpre.img-cn-beijing.aliyuncs.com/test/dir2/20190218/cdrb_2_2015034293af6a-36c3-4c54-900f-4545c0f8eee0.jpg"
    }
} */
	@RequestMapping(value = "file", method = RequestMethod.POST)
	@ApiOperation("单个文件上传")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "file", value = "文件", paramType = "body", required = true),
			@ApiImplicitParam(name = "keyword", value = "关键字", paramType = "query", required = true)
	})
	public ResultVO2<Map<String,Object>> upload(@RequestParam("file") MultipartFile file, @RequestParam(required = false) String keyword) {
		String fileName = file.getOriginalFilename();
		//文件类型检查
		String[] names = fileName.split("\\.");
		if (names.length <= 1) {
			return ResultVO2.error("文件格式不正确");
		}
		String fileType = names[names.length - 1];
		if (!Lists.newArrayList(fileTypes.split(",")).contains(fileType.toLowerCase())) {
			return ResultVO2.error("文件类型只支持" + fileTypes + "类型");
		}
		//图片大小检查
        if(ImageChecker.isImage(fileType)){
		    if(file.getSize() > imageSize){
		        return ResultVO2.error("上传图片大小不能超过"+imageSize/1024/1024+"M");
            }
        }
		//媒体资源大小检查
		if(MediaChecker.isMedia(fileType)){
			if(file.getSize() > mediaSize){
				return ResultVO2.error("上传资源大小不能超过"+mediaSize/1024/1024+"M");
			}
		}
		return ResultVO2.success(uploadService.upload(fileType, file, keyword),"文件上传成功");
	}

	@RequestMapping(value = "files", method = RequestMethod.POST)
	@ApiOperation("多个文件上传")
	public ResultVO2<List<Map<String, Object>>> uploads(@RequestParam("file") MultipartFile[] files) {
		Map<MultipartFile, String> types = Maps.newHashMap();
		//检查所有上传文件的类型，如有一个不合格则都不上传
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].getOriginalFilename();
			//文件类型检查
			String[] names = fileName.split("\\.");
			if (names.length <= 1) {
				return ResultVO2.error(fileName + "文件格式不正确");
			}
			String fileType = names[names.length - 1];
			if (!Lists.newArrayList(fileTypes.split(",")).contains(fileType)) {
				return ResultVO2.error(fileName + "格式不正确，文件类型只支持" + fileTypes + "类型");
			}
			//媒体资源大小检查
			if(MediaChecker.isMedia(fileType)){
				if(files[i].getSize() > mediaSize){
					return ResultVO2.error("上传资源大小不能超过"+mediaSize/1024/1024+"M");
				}
			}
			types.put(files[i], fileType);
		}
		//上传所有的文件
		List<Map<String, Object>> list = Lists.newArrayList();
		types.forEach((file, type) -> list.add(uploadService.upload(type, file, null)));
		return ResultVO2.success( list,"文件上传成功");
	}

    /**
     * 查询上传状态
     * @param mediaInfoRequestVO
     * @return
     */
	@ApiOperation("上传结果查询")
    @RequestMapping(value = "checkUploadStatus", method = RequestMethod.POST)
    public ResultVO2<MediaInfoCheckStatusVO> checkUploadStatus(@RequestBody MediaInfoRequestVO mediaInfoRequestVO) {
        String fileUrls = mediaInfoRequestVO.getFileUrls();
        if(Lang.isEmpty(fileUrls)){
            return ResultVO2.error("参数不能为空！！！");
        }
        return ResultVO2.success(uploadService.checkUploadStatus(fileUrls));
    }
}
