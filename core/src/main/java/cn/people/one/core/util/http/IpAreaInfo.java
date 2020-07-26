package cn.people.one.core.util.http;

import cn.people.one.core.util.text.StringUtils;
import lombok.Data;

@Data
public class IpAreaInfo {

	private String country = "";// 国家
	private String province = "中国";//省
	private String city = "";// 市
	private String county = "";// 区县

	public String toString() {
		return "IpInfo [country=" + country + ", province=" + province
				+ ", city=" + city + ", county=" + county + "]";
	}

	/**
	 * 拼接address地址
	 * @return
	 */
	public String getAddrss(){
		StringBuffer address=new StringBuffer("");
		if(StringUtils.isNotBlank(country)){
			address.append(country);
		}
		if(StringUtils.isNotBlank(province)){
			address.append("-"+province);
		}
		if (StringUtils.isNotBlank(city)) {
			address.append("-"+city);
		}
		if(StringUtils.isNotBlank(county)){
			address.append("-"+county);
		}
		return address.toString();
	}

}
