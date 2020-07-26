package cn.people.one.modules.ask.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.ask.model.AskGovernment;
import cn.people.one.modules.ask.model.AskGovernmentType;
import cn.people.one.modules.ask.model.front.AskGovernmentVO;

import java.util.List;

public interface IAskGovernmentService extends IBaseService<AskGovernment>{
    /**
     * 
     * @Description: 获取所有本地机构
     * @return List<AskGovernmentVO>机构列表
     */
    List<AskGovernmentVO> getAllLocalGov();
    /**
     * 
     * @Description: 获取下级机构id，包括下级机构的下级，递归查找
     * @return List<Integer>机构id列表
     */
    List<Long> traverseChild(Long fid);
    /**
     * @Description: 根据id获取部门信息
     * @return List<Integer>部门信息
     */
    AskGovernment getGovernmentById(Long id);
    /**
     * @Description: 根据id获取部门名称，没有返回“”
     * @return List<Integer>部门信息
     */
    String getNameById(Long id);
    /**
     * 清空缓存
     */
    void clearCache();

    /**
     *获取区域选择信息（表ask_government）
     * @return
     */
    List<AskGovernmentType> getAllArea(Long gid);
    AskGovernment getGovernmentByFup(Long id);
}