package cn.people.one.modules.cms.model.type;

/**
 * @author YX
 * @date 2019-05-14
 * @comment
 */
public enum CategoryModelEnum {
    RECOMMEND(6L),//推荐类型

    PAPER(7L);//报纸类型

    private final Long value;

    CategoryModelEnum(Long value) {
        this.value = value;
    }

    public Long value() {
        return this.value;
    }
}
