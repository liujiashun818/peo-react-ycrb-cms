package cn.people.one.modules.cms.model.type;

/**
 * Created by lml on 2017/4/1.
 */
public enum ViewType {
    BANNER("banner"),//通栏

    NORMAL("normal"),//非通栏(左图右文)

    THREE("three");//三图
    private final String value;

    ViewType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
