package cn.people.one.core.base.dao;

import org.nutz.dao.Cnd;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * DAO的基类
 */
//@Configuration
//@Repository
public class SecondDao extends NutDao implements Dao {
    @Autowired
    DataSource druidDataSource;

    public Cnd getBaseCondition(){
        return Cnd.where("del_flag","=","0");
    }

    @Autowired   //这里注解只能写到set方法上，不然druidDataSource获取不到
    public void setDruidDataSource(@Qualifier("secondDruidDataSource") DataSource druidDataSource) {
        this.druidDataSource = druidDataSource;
        setDataSource(druidDataSource);
    }

    public void run(ConnCallback callback) {
        Connection con = DataSourceUtils.getConnection(druidDataSource);
        try {
            callback.invoke(con);
        } catch (Exception e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else
                throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(con, druidDataSource);
        }
    }

}
