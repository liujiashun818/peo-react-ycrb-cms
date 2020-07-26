package cn.people.one.modules.sys.service.impl;

import cn.people.one.core.util.http.IPHelper;
import cn.people.one.core.util.http.IpAreaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.people.one.modules.sys.service.IPService;


@Service
public class IPServiceImpl implements IPService{

	private static Logger log = LoggerFactory.getLogger("support.api");
	/**
	 * 根据ip 查询ip区域信息
	 * @param ip
	 * @return
	 */
	public IpAreaInfo findIpArea(String ip){
		log.info("调用 >>>findIpArea(), 参数：{}",ip);
		String[] ipArr = IPHelper.findIp(ip);
		log.debug("调用 >>>findIpArea(), 结束.");
		return IPHelper.transformInfo(ipArr);
	}
}
