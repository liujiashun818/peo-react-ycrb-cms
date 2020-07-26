package cn.people.one.modules.client.constant;

/**
 * Created by Cheng on 2017/3/12.
 */
public enum EClientMenuPosition {
    /**
     * 展示位置（1默认、2首次置顶、3总是置顶、4固定置顶）
     */
    NORMAL("NORMAL"),
    ONE_TOP("ONE_TOP"),
    ALWAYS_TOP("ALWAYS_TOP"),
    FIX("FIX");

    private final String value;
    EClientMenuPosition(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
}
