package cn.people.one.appapi.converter;

import cn.people.one.appapi.vo.PageVO;
import org.nutz.dao.pager.Pager;

/**
 * Created by wilson on 2018-10-10.
 */
public class PageConverter {

    public static PageVO toPageVO(Pager pager) {
        if (pager == null) {
            return PageVO.emptyPage();
        }
        return toPageVO((long) pager.getRecordCount(), pager.getPageSize(), pager.getPageNumber());
    }

    public static PageVO toPageVO(long count, int size, int page) {
        if (count < 1 || size < 1 || page < 1) {
            return PageVO.emptyPage();
        }

        int last = (int) Math.ceil((double) count / size);
        if (page > last) {
            return PageVO.emptyPage();
        }

        PageVO vo = new PageVO();
        vo.setCount(count);
        vo.setFirst("1");

        if (page == 1) {
            vo.setPrevious("");
        } else {
            vo.setPrevious(String.valueOf(page - 1));
        }

        if (page == last) {
            vo.setNext("");
        } else {
            vo.setNext(String.valueOf(page + 1));
        }

        vo.setLast(String.valueOf(last));
        return vo;
    }

}
