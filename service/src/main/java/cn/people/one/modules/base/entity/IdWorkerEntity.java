package cn.people.one.modules.base.entity;

import lombok.Data;
import org.nutz.dao.entity.annotation.Id;

import java.io.Serializable;

/**
 * 数据Entity类
 */
@Data
public abstract class IdWorkerEntity<T> extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private static IdWorker idWorker = new IdWorker(1);

	/**
	 * 主键id
	 */
	@Id(auto = false)
	protected Long id;


	@Override
	public void init(){
		this.id = idWorker.nextId();
	}

	public IdWorkerEntity() {
	}

	public IdWorkerEntity(Long id) {
		this.id = id;
	}
}
