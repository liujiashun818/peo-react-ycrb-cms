package cn.people.one.modules.cms.model.type;

/**
 * Created by lml on 17-3-1.
 */
public enum ArticleType {

    /**
     * 文章类型枚举类 除引用、专题、直播之外其他是正常的文章类型
     */

    NORMAL("common"),   //图文
    SUBJECT("subject"), //专题
    LIVE("live"),      //直播
    IMAGE("image"),//图片类型
    ASK("ask"),//问政
    VIDEO("video"),//视频类型
    AUDIO("audio"),//音频类型
    NORMAL_("normal"),
    LINK("link");//链接类型

    private final String value;

    ArticleType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
