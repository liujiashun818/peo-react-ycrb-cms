package cn.people.one.modules.sys.model.front;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * User: 张新征
 * Date: 2017/4/11 16:18
 * Description:
 */
@Data
public class LogVO {

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date beginTime;//查询起始时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endTime;//查询截止时间
	private Long userId;
	private String URI;
	private Integer isException;
	private Integer pageNumber;
	private Integer pageSize;
}
