package cn.people.one.core.base.api;

import io.swagger.annotations.ApiModel;
import org.nutz.castor.Castors;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 在QueryResult基础上进一步封装列表查询结果
 * 完善泛型
 * @author YX
 * @date 2019-05-08
 * @comment
 */
@ApiModel
public class QueryResultVO<T> extends QueryResult{
    private static final long serialVersionUID = 5104522523949248573L;

    public QueryResultVO(List<T> list, Pager pager) {
        super(list, pager);
    }

    @Override
    public List<T> getList() {
        return (List<T>) super.getList();
    }

    @Override
    public <T> List<T> getList(Class<T> eleType) {
        return (List<T>) super.getList();
    }

    @Override
    public <T> List<T> convertList(Class<T> eleType) {
        if (null != super.getList() && !super.getList().isEmpty()) {
            List<T> re = new ArrayList(super.getList().size());
            Castors castors = Castors.me();
            Iterator var4 = super.getList().iterator();

            while(var4.hasNext()) {
                Object obj = var4.next();
                re.add(castors.castTo(obj, eleType));
            }

            return re;
        } else {
            return (List<T>)super.getList();
        }
    }
}
