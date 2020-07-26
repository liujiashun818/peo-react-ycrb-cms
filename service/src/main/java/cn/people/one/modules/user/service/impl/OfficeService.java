package cn.people.one.modules.user.service.impl;

import cn.people.one.core.aop.annotation.ExCacheEvict;
import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.modules.base.config.RedisConfig;
import cn.people.one.modules.base.service.impl.TreeService;
import cn.people.one.modules.user.model.Office;
import cn.people.one.modules.user.model.front.NavOfficeVO;
import cn.people.one.modules.user.service.IOfficeService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* 用户管理下机构管理Service
* @author cuiyukun
*/
@Service
@Transactional(readOnly = true)
public class OfficeService extends TreeService<Office> implements IOfficeService {

    @Autowired
    private BaseDao dao;

    /**
     * 增加一条机构记录
     *
     * @param office
     */
    @Override
    @ExCacheEvict(value = {RedisConfig.OFFICE_TREE})
    @Transactional
    public Office save(Office office)  {
        return super.save(office);
    }

    /**
     * 伪删除角色
     *
     * @param id
     * @return
     */
    @Override
    @ExCacheEvict(value = {RedisConfig.OFFICE_TREE})
    @Transactional
    public int vDelete(Long id) {
        return super.vDelete(id);
    }

    /**
     * 获取机构列表并分页
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public QueryResultVO<Office> listPage(Integer pageNumber, Integer pageSize) {
        return super.listPage(pageNumber, pageSize, Cnd.NEW());
    }

    /**
     * 返回所有机构列表
     *
     * @return
     */
    @Override
    public List<Office> listAll() {
        List<Office> allOffices = query(null, Cnd.where("del_flag","<",BaseEntity.STATUS_DELETE));
        return allOffices;
    }

    /**
     * 返回机构树(全部)
     * @return
     */
    @Override
    @Cacheable(value = RedisConfig.OFFICE_TREE)
    public List<NavOfficeVO>getOfficeTree(Long id){
        return getTree(id,null);
    }

    /**
     * 返回机构树(上线)
     * @return
     */
    @Override
    @Cacheable(value = RedisConfig.OFFICE_TREE)
    public List<NavOfficeVO>getOfficeTree(Long id,Integer delFlag){
        return getTree(id,delFlag);
    }

    public List<NavOfficeVO> getTree(Long id,Integer delFlag) {
        List<Office> sourceList = queryByParentId(id,delFlag);
        List<NavOfficeVO> list = BeanMapper.mapList(sourceList, NavOfficeVO.class);
        if(null!=list && list.size() > 0){
            list.stream().forEach(office->recursive(office,delFlag));
        }
        return list;
    }

    private NavOfficeVO recursive(NavOfficeVO officeVO,Integer delFlag){
        List<Office> list = queryByParentId(officeVO.getId(),delFlag);
        List<NavOfficeVO> child = BeanMapper.mapList(list, NavOfficeVO.class);
        if(null!=child && child.size() > 0){
            child.stream().forEach(office->recursive(office,delFlag));
        }
        officeVO.setChild(child);
        return officeVO;
    }

    @Override
    @ExCacheEvict(value = {RedisConfig.OFFICE_TREE})
    @Transactional
    public int updateIgnoreNull(Office office){
        return super.updateIgnoreNull(office);
    }

    /**
     *
     * @param upmsofficeId
     * @return
     */
    @Override
    public Office getOfficeByUpmsOfficeId(String upmsofficeId) {
        return dao.fetch(Office.class,Cnd.where(Office.FIELD_STATUS,"=",Office.STATUS_ONLINE).and(Office.Constant.UPMSOFFICEID,"=",upmsofficeId));
    }
}