package cn.people.one.modules.cms.model.type;

/**
 * Created by lml on 17-3-1.
 */
public enum SysCodeType {
    /**
     * 选择新建文章时，可选择的系统模块
     */
    SUBJECT("subject"),//专题

    LIVE("live"),//直播

    ARTICLE("article"),//文章

    /**
     * 问政
     */
    ASK("ask"),
    /**
     * 报纸
     */
    PAPER("paper"),
    /**
     * 政务
     */
    GOV("gov");
    private final String value;

    SysCodeType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
