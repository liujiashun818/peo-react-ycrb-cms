package cn.people.one.modules.app.service;

import cn.people.one.modules.app.model.Keywords;
import cn.people.one.modules.base.service.IBaseService;

import java.util.List;

/**
* 搜索关键词Service
* @author Cheng
*/
public interface IKeywordsService extends IBaseService<Keywords> {

    List<Keywords> appKeywordsList();
}