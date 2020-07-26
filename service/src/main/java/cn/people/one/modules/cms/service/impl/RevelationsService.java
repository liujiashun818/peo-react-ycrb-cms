package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.util.http.IPHelper;
import cn.people.one.core.util.http.IpAreaInfo;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.cms.model.Revelations;
import cn.people.one.modules.cms.service.IRevelationsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.sql.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RevelationsService extends BaseService<Revelations> implements IRevelationsService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public QueryResultVO<Revelations> listPage(Integer pageNo, Integer pageSize) {
        QueryResultVO<Revelations> result = null;
        try {
            Cnd cnd= org.nutz.dao.Cnd.where("del_flag", " = ", 0);
            result = super.listPage(pageNo, pageSize, cnd);
            for (Revelations vo : result.getList()) {
            	if (vo.getIp().isEmpty()) {
                    String[] remarks;
            		if (vo.getRemarks().contains("IpInfo")) {
            			remarks = vo.getRemarks().split("\\(");
					}else {
						remarks = vo.getRemarks().split("\\{");
					}
                    vo.setIp(remarks[0]);
                    vo.setAddress(IPHelper.getAreaInfo(remarks[0]).getAddrss());
				}
            	result.setList(result.getList());
			}
            return result;
        }catch (Exception e){
            log.error(e.getMessage());
            return result;
        }

    }

    @Override
    public QueryResultVO<Revelations> findListByName(Integer pageNumber, Integer pageSize, String name) {
        QueryResultVO<Revelations> result = null;
        Criteria cri = Cnd.cri();
        cri.where().and(BaseEntity.FIELD_STATUS, " = ", 0);
        if (null != name) {
            cri.where().and("name", "like", "%" + name + "%");
        }
        cri.getOrderBy().desc("id");
        try {
            result = listPage(pageNumber, pageSize, cri);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 保存新闻爆料
     * @param revelations 新闻报料对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveRevelations(Revelations revelations) {
        IpAreaInfo ipAreaInfo=IPHelper.getAreaInfo(revelations.getIp());
        if(ipAreaInfo!=null){
            String remarks=revelations.getIp()+ipAreaInfo.toString();
            revelations.setRemarks(remarks);
            revelations.setAddress(ipAreaInfo.getAddrss());
        }
        save(revelations);
    }


    /**
     * 查询新闻爆料详情
     * @param id
     * @return
     */
    @Override
    public Revelations getRevelationsById(Long id) {
        Revelations revelations=fetch(id);
        if (revelations!=null && revelations.getIp().isEmpty()) {
            String[] remarks;
            if (revelations.getRemarks().contains("IpInfo")) {
                remarks = revelations.getRemarks().split("\\(");
            }else {
                remarks = revelations.getRemarks().split("\\{");
            }
            revelations.setIp(remarks[0]);
            revelations.setAddress(IPHelper.getAreaInfo(remarks[0]).getAddrss());
        }
        return revelations;
    }
}
