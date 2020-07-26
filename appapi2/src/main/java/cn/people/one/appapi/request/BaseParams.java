package cn.people.one.appapi.request;

/**
 * Created by wilson on 2018-11-30.
 */
public class BaseParams {
    private static ThreadLocal<BaseParams> BASE_PARAMS_THREAD_LOCAL = new ThreadLocal<>();

    private String pjCode;
    private String clientVersion;
    private String clientVersionCode;
    private String platform;
    private String deviceOs;
    private String deviceModel;
    private String deviceSize;
    private String udid;
    private String channel;

    public String getPjCode() {
        return pjCode;
    }

    public void setPjCode(String pjCode) {
        this.pjCode = pjCode;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getClientVersionCode() {
        return clientVersionCode;
    }

    public void setClientVersionCode(String clientVersionCode) {
        this.clientVersionCode = clientVersionCode;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDeviceOs() {
        return deviceOs;
    }

    public void setDeviceOs(String deviceOs) {
        this.deviceOs = deviceOs;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceSize() {
        return deviceSize;
    }

    public void setDeviceSize(String deviceSize) {
        this.deviceSize = deviceSize;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public static void setParams(BaseParams params) {
        BASE_PARAMS_THREAD_LOCAL.set(params);
    }

    public static BaseParams getParams() {
        return BASE_PARAMS_THREAD_LOCAL.get();
    }
}
