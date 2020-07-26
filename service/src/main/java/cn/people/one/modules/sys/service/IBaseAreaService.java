package cn.people.one.modules.sys.service;

import java.util.List;

import cn.people.one.modules.sys.model.BaseArea;


public interface IBaseAreaService {

	/**
	 * 查询全部城市
	 * @return
	 */
	public List<BaseArea> queryAllBaseArea();
	
	/**
	 * 根据父id查询城市
	 * @param pid
	 * @return
	 */
	public List<BaseArea> queryBaseAreaByPid(String pid);
	
	/**
	 * 根据等级 查询城市
	 * @param level
	 * @return
	 */
	public List<BaseArea> queryBaseAreaByLevel(String level);
}
