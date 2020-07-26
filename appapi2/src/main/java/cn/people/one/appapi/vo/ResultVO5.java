package cn.people.one.appapi.vo;

import cn.people.one.appapi.converter.PageConverter;
import cn.people.one.appapi.model.ErrorMessage;
import cn.people.one.core.base.api.Result;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by wilson on 2018-10-09.
 */
@ApiModel(value = "Result", description = "返回响应数据")
public class ResultVO5<T,E> {

    @ApiModelProperty(value = "服务状态码 "+Result.DEFAULT_CODE_SUCCESS+"：成功...")
    private int code;

    @ApiModelProperty(value = "信息")
    private String msg;

    @ApiModelProperty(value = "数据")
    private T item;
    
    @ApiModelProperty(value = "数据")
    private List<E> items;
    
    @ApiModelProperty(value = "配合对象列表使用，用于分页相关的数据")
    private PageVO page;

    public PageVO getPage() {
		return page;
	}

	public void setPage(PageVO page) {
		this.page = page;
	}

	public static <T,E> ResultVO5<T,E> result(ErrorMessage message) {
        ResultVO5<T,E> rj = new ResultVO5<T,E>();
        rj.setCode(message.getCode());
        rj.setMsg(message.getMsg());
        return rj;
    }

    public static <T,E> ResultVO5<T,E> result(int code, String message) {
        ResultVO5<T,E> rj = new ResultVO5<T,E>();
        rj.setCode(code);
        rj.setMsg(message);
        return rj;
    }

    public static <T,E> ResultVO5<T,E> error(String message) {
        return ResultVO5.result(Result.DEFAULT_CODE_ERROR, message);
    }


    public static <T,E> ResultVO5<T,E> success(T item, List<E> items, long count, int size, int page) {

        ResultVO5<T,E> rj = new ResultVO5<T,E>();
        rj.code = Result.DEFAULT_CODE_SUCCESS;
        rj.msg = "result";
        rj.setItem(item);
        rj.setItems(items);
        rj.page = PageConverter.toPageVO(count, size, page);
        return rj;
    }

//
//    public ResultVO2<T>(ErrorMessage message) {
//        this.code = message.getCode();
//        this.msg = message.getMsg();
//    }
//
//    public ResultVO2<T>(int code, String msg) {
//        this.code = code;
//        this.msg = msg;
//    }
//
//    public <T> ResultVO2<T>(T item) {
//        this.code = 0;
//        this.msg = "result";
//        this.item = item;
//    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

	public List<E> getItems() {
		return items;
	}

	public void setItems(List<E> items) {
		this.items = items;
	}
    
}
