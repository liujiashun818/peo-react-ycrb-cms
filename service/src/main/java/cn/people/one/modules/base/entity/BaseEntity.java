package cn.people.one.modules.base.entity;

import lombok.Data;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;

/**
 * @author YX
 * @date 2019-03-01
 * @comment
 */
@Data
public abstract class BaseEntity extends BaseModel{

    private static final long serialVersionUID = 1L;
    @Id
    @Comment("主键")
    private Long id;

    public BaseEntity(Long id) {
        this.id=id;
    }

    public BaseEntity() {
    }
}
