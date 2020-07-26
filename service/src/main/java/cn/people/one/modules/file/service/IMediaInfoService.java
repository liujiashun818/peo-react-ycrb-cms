package cn.people.one.modules.file.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.file.model.MediaInfo;

import java.util.List;

/**
* 媒体信息Service
* @author zxz
*/
public interface IMediaInfoService extends IBaseService<MediaInfo>{

    /**
     * 关键字检索
     */
    List<MediaInfo> keyword(String keyword);
}