package cn.people.one.modules.sys.service;


import cn.people.one.core.util.http.IpAreaInfo;

public interface IPService {

	public IpAreaInfo findIpArea(String ip);
	
}
