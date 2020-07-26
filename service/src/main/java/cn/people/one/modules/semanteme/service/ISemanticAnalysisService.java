package cn.people.one.modules.semanteme.service;

import cn.people.one.modules.semanteme.model.SemantemeRequest;
import cn.people.one.modules.semanteme.model.SemantemeResult;

public interface ISemanticAnalysisService {

	/**
	 * 获得主题词
	 * @param req
	 * @return
	 */
	public SemantemeResult getKeywords(SemantemeRequest req);
	
	/**
	 * 获取摘要
	 * @param req
	 * @return
	 */
	public SemantemeResult getSummary(SemantemeRequest req);
}
