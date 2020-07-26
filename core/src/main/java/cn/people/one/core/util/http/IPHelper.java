package cn.people.one.core.util.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.*;

@Slf4j
public class IPHelper{

	private static ResourceLoader resourceLoader = new DefaultResourceLoader();
	private static String ipDataAddr = "17monipdb.dat";
	/**
	 * 获取 ip地址信息数组
	 * @param ip
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String[] findIp(String ip){
		if(StringUtils.isEmpty(ip)){
			log.error("调用findIp方法，参数ip为空！");
			return null;
		}
		String[] ipAreaArr = null;
		String filename = getIpDataFilePath();
		if(filename == null || "".equals(filename)){
			return null;
		}
		try {
			IP.load(filename);
			ipAreaArr = IP.find(ip);
		} catch (Exception e) {
			log.error("获取ip地区信息异常",e);
			ipAreaArr = null;
		}
		return ipAreaArr;
	}
	/**
	 * 获取IpData文件路径
	 * @return
	 */
	private static String getIpDataFilePath(){
		String filePath = null;
		try {
			filePath = ResourceUtils.getFile("classpath:"+ipDataAddr).getAbsolutePath();
		} catch (Exception e) {
			log.error("获取文件"+ipDataAddr+"失败.",e);
			filePath = null;
		}
		return filePath;
	}
	/**
	 * 获取 ip地址信息数组
	 */
	public static String[] findIp(String ip, File file){
		if(StringUtils.isEmpty(ip)){
			log.error("调用findIp方法，参数ip为空！");
			return null;
		}
		String[] ipAreaArr;
		if(file == null || file.length()==0){
			return null;
		}
		try {
			IP.load(file);
			ipAreaArr = IP.find(ip);
		} catch (Exception e) {
			log.error("获取ip地区信息异常",e);
			ipAreaArr = null;
		}
		return ipAreaArr;
	}

	/**
	 * 结果转换
	 */
	public static IpAreaInfo transformInfo(String[] arr){
		IpAreaInfo info = new IpAreaInfo();
		if(arr == null || arr.length < 1){
			return info;
		}
		if(arr.length > 0){
			if("局域网".equals(arr[0])){
				info.setCountry("中国");
			}else{
				info.setCountry(arr[0]);
			}
		}
		if(arr.length > 1){
			if("局域网".equals(arr[1])){
				info.setProvince("北京");
			}else{
				info.setProvince(arr[1]);
			}
		}
		if(arr.length > 2){
			info.setCity(arr[2]);
		}
		if(arr.length > 3){
			info.setCounty(arr[3]);
		}
		return info;
	}

	/**
	 * 根据IP获取区域地址
	 * @param ip
	 * @return
	 */
	public static IpAreaInfo getAreaInfo(String ip){
		File file = new File("ip.tmp");
		IpAreaInfo area=null;
		try {
			InputStream in = resourceLoader.getResource("classpath:17monipdb.dat").getInputStream();
			file.deleteOnExit();
			OutputStream out = new FileOutputStream(file);
			IOUtils.copy(in, out);
			out.close();
			in.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		String[] ipArr = IPHelper.findIp(ip, file);
		area = IPHelper.transformInfo(ipArr);
		return area;
	}


}
