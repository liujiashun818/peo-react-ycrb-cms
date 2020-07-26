package cn.people.one.modules.user.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.ITreeService;
import cn.people.one.modules.user.model.Office;
import cn.people.one.modules.user.model.front.NavOfficeVO;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
* 用户管理下机构管理Service
* @author cuiyukun
*/
public interface IOfficeService extends ITreeService<Office> {

    List<NavOfficeVO> getOfficeTree(Long id,Integer delFlag);

    List<NavOfficeVO>getOfficeTree(Long id);

    QueryResultVO<Office> listPage(Integer pageNumber, Integer pageSize);

    List<Office> listAll();

    Office getOfficeByUpmsOfficeId(String upmsofficeId);
}