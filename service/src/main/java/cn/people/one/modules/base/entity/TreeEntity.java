package cn.people.one.modules.base.entity;

import cn.people.one.core.util.reflect.ReflectionUtil;
import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;

@Data
public abstract class TreeEntity<T> extends BaseEntity {

	private static final long serialVersionUID = 1L;

	protected T parent;	// 父级菜单
	@Column(hump = true)
	@Comment("父级编号")
	private Long parentId;// 父级编号
	@Column(hump = true)
	@Comment("所有父级编号")
	protected String parentIds; // 所有父级编号
	@Column
	@Comment("名称")
	protected String name; 	// 名称
	@Column
	@Comment("排序")
	protected Integer sort;		// 排序
	
	public TreeEntity() {
		super();
	}
	
	public TreeEntity(Long id) {
		super(id);
	}
	
	public Long getParentId() {
		Long id = null;
		if(null !=parentId){
			id = parentId;
		}else if (parent != null){
			id = ReflectionUtil.getFieldValue(parent, "id");
		}
		return id;
	}

	@Override
	public void init(){
		//设置默认排序值
		if(sort == null){
			sort = 30;
		}
		//设置默认状态
		super.init();
	}
}
