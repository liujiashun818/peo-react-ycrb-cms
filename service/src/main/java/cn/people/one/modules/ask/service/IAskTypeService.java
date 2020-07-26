package cn.people.one.modules.ask.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.ask.model.AskDomainResp;
import cn.people.one.modules.ask.model.AskType;

import java.util.List;

public interface IAskTypeService extends IBaseService<AskType>{
    /**
     * @Description:根据id获取问题类型
     * @return 问题类型对象
     */
    AskType getTypeByid(int typeId);
    /**
     * @Description:根据id获取问题类型名称，没有则返回“”
     * @return 问题类型名称
     */
    String getNameByid(int typeId);
    /**
     * @Description:获取所有问题类型
     * @return 问题类型列表
     */
    List<AskType> getAllTypes();
    
    /**
     * 清空缓存
     */
    void clearCache();

    /**
     * 获取所有问题领域类型的id和name（对应表ask_type）
     * @return
     */
    List<AskDomainResp> getAllDomainsIdAndName();
}