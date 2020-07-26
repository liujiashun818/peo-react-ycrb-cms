package cn.people.one.appapi.util;

import cn.people.one.appapi.request.BaseParams;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wilson on 2018-11-30.
 */
public class BaseParamsUtils {

    public static void setBaseParams(HttpServletRequest request) {
        BaseParams params = new BaseParams();

        String pjCode = request.getParameter("pjCode");
        if (StringUtils.isNotBlank(pjCode)) {
            params.setPjCode(pjCode);
        }

        String clientVersion = request.getParameter("clientVersion");
        if (StringUtils.isNotBlank(clientVersion)) {
            params.setClientVersion(clientVersion);
        }

        String clientVersionCode = request.getParameter("clientVersionCode");
        if (StringUtils.isNotBlank(clientVersionCode)) {
            params.setClientVersionCode(clientVersionCode);
        }

        String deviceSize = request.getParameter("device_size");
        if (StringUtils.isNotBlank(deviceSize)) {
            params.setDeviceSize(deviceSize);
        }

        String deviceOs = request.getParameter("deviceOs");
        if (StringUtils.isNotBlank(deviceOs)) {
            params.setDeviceOs(deviceOs);
        }

        String deviceModel = request.getParameter("deviceModel");
        if (StringUtils.isNotBlank(deviceModel)) {
            params.setDeviceModel(deviceModel);
        }

        String channel = request.getParameter("channel");
        if (StringUtils.isNotBlank(channel)) {
            params.setChannel(channel);
        }

        String platform = request.getParameter("platform");
        if (StringUtils.isNotBlank(platform)) {
            params.setPlatform(platform);
        }

        String udid = request.getParameter("udid");
        if (StringUtils.isNotBlank(udid)) {
            params.setUdid(udid);
        }

        BaseParams.setParams(params);
    }

}
