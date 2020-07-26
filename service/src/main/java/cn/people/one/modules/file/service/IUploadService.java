package cn.people.one.modules.file.service;

import cn.people.one.modules.file.model.front.MediaInfoCheckStatusVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by zhangxinzheng on 2017/2/13.
 */
public interface IUploadService {

//    Map<String,Object> upload(String fileType, MultipartFile file, String keyword);

    MediaInfoCheckStatusVO checkUploadStatus(String fileUrls);

    Map<String,Object> upload(String fileType, MultipartFile file, String keyword);
    void saveContent(String filePath, String content);
    void saveFile(InputStream inputStream, String path, String fileName);
}
