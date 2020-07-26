package cn.people.one.appapi.vo;

import cn.people.one.appapi.converter.PageConverter;
import cn.people.one.appapi.model.ErrorMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.nutz.dao.QueryResult;

import java.util.Collections;
import java.util.List;

/**
 * Created by wilson on 2018-10-09.
 */
@ApiModel(value = "Result", description = "返回响应数据")
public class ResultVO {

    @ApiModelProperty(value = "服务器代码")
    private int code;

    @ApiModelProperty(value = "代码说明")
    private String msg;

    @ApiModelProperty(value = "用于扩展说明的详细信息")
    private Object detail;

    @ApiModelProperty(value = "API返回的单个对象")
    private Object item;

    @ApiModelProperty(value = "配合对象列表使用，用于分页相关的数据")
    private PageVO page;

    @ApiModelProperty(value = "API返回的对象列表")
    private List<?> items;

    public static ResultVO result(ErrorMessage message) {
        return new ResultVO(message);
    }

    public static ResultVO result(int code, String message) {
        return new ResultVO(code, message);
    }

    public static ResultVO result(String message) {
        return new ResultVO(0, message);
    }

    public static ResultVO result(List<?> items, long count, int size, int page) {
        return new ResultVO(items, count, size, page);
    }

    public static ResultVO result(Object item) {
        return new ResultVO(item);
    }

    public static ResultVO result(QueryResult result) {
        return new ResultVO(result);
    }

    public static ResultVO result(Object item, List<?> items) {
        return new ResultVO(item, items);
    }

    public static ResultVO result(Object item, List<?> items, long count, int size, int page) {
        return new ResultVO(item, items, count, size, page);
    }

    public ResultVO() {
    }

    public ResultVO(ErrorMessage message) {
        this.code = message.getCode();
        this.msg = message.getMsg();
    }

    public ResultVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(Object item) {
        this.code = 0;
        this.msg = "result";
        this.item = item;
    }

    public ResultVO(QueryResult result) {
        this.code = 0;
        this.msg = "result";
        this.items = result.getList();
        this.page = PageConverter.toPageVO(result.getPager());
    }

    /**
     * @param items
     * @param count
     * @param page  当前页
     * @param size
     */
    public ResultVO(List<?> items, long count, int size, int page) {
        this.code = 0;
        this.msg = "result";
        this.items = items;
        this.page = PageConverter.toPageVO(count, size, page);
    }

    /**
     * @param items
     * @param count
     * @param page  当前页
     * @param size
     */
    public ResultVO(Object item, List<?> items, long count, int size, int page) {
        this.code = 0;
        this.msg = "result";
        this.item = item;
        this.items = items;
        this.page = PageConverter.toPageVO(count, size, page);
    }

    public ResultVO(Object item, List<?> items) {
        this.code = 0;
        this.msg = "result";
        this.item = item;
        this.items = items;
    }

    /**
     *
     * @param item
     * @param result
     */
    public ResultVO(Object item, QueryResult result) {
        this.code = 0;
        this.msg = "result";
        this.item=item;
        this.items = result.getList();
        this.page = PageConverter.toPageVO(result.getPager());
    }
    public static ResultVO emptyList() {
        return new ResultVO(Collections.EMPTY_LIST, 0, 0, 1);
    }

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

    public Object getDetail() {
        return detail;
    }

    public void setDetail(Object detail) {
        this.detail = detail;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public PageVO getPage() {
        return page;
    }

    public void setPage(PageVO page) {
        this.page = page;
    }

    public List<?> getItems() {
        return items;
    }

    public void setItems(List<Object> items) {
        this.items = items;
    }
}
