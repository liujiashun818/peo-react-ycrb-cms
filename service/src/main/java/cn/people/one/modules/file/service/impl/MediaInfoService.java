package cn.people.one.modules.file.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.file.model.MediaInfo;
import cn.people.one.modules.file.service.IMediaInfoService;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* 媒体信息Service
* @author zxz
*/
@Service
public class MediaInfoService extends BaseService<MediaInfo> implements IMediaInfoService {

    @Autowired
    private BaseDao dao;

    @Override
    public List<MediaInfo> keyword(String keyword) {
        if(StringUtils.isBlank(keyword)){
            return null;
        }
        Condition condition = Cnd.where("status", "=", 1).and("del_flag", "=", 0).and("name", "like", "%"+keyword+"%").or("keyword", "like", "%"+keyword+"%").desc("trans_time");;
        return dao.query(MediaInfo.class, condition);
    }
}