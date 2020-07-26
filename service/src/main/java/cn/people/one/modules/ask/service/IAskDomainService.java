package cn.people.one.modules.ask.service;

import cn.people.one.modules.ask.model.AskDomain;
import cn.people.one.modules.ask.model.AskDomainResp;
import cn.people.one.modules.base.service.IBaseService;

import java.util.List;

public interface IAskDomainService extends IBaseService<AskDomain> {
    /**
     * 
     * @Description: 根据id获取问题领域
     */
    AskDomain getDomainByid(int id);
    /**
     * 
     * @Description: 根据id获取问题领域名称，没有则返回“”
     */
    String getNameByid(int id);
    /**
     * 
     * @Description: 获取所有问题领域
     */
    List<AskDomain> getAllDomains();
    /**
     * 清空缓存
     */
    void clearCache();

    /**
     * 获取所有问题领域的id和name（对应表ask_domain）
     * @return
     */
    List<AskDomainResp> getAllDomainsIdAndName();

}