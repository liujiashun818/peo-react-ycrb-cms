package cn.people.one.appapi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wilson on 2018-10-09.
 */
@Data
@ApiModel(value = "Page", description = "关于list的分页数据", parent = ResultVO.class)
public class PageVO {

    private static final PageVO EMPTY_PAGE;

    static {
        PageVO page = new PageVO();
        page.setCount(0L);
        page.setFirst("");
        page.setPrevious("");
        page.setNext("");
        page.setLast("");
        EMPTY_PAGE = page;
    }

    @ApiModelProperty(value = "数据总量")
    private Long count;

    @ApiModelProperty(value = "第一页，此值用于请求参数pageToken")
    private String first;

    @ApiModelProperty(value = "上一页，此值用于请求参数pageToken")
    private String previous;

    @ApiModelProperty(value = "下一页，此值用于请求参数pageToken")
    private String next;

    @ApiModelProperty(value = "最后一页，此值用于请求参数pageToken")
    private String last;

    public static PageVO emptyPage() {
        return EMPTY_PAGE;
    }

}
