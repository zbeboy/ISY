package top.zbeboy.isy.test;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Before;
import org.junit.Test;
import top.zbeboy.isy.domain.tables.records.UsersRecord;
import static top.zbeboy.isy.domain.Tables.*;
/**
 * Created by zbeboy on 2016/11/7.
 */
public class TestData {

    DSLContext create;

    @Before
    public void init(){
        // Initialise your data provider (implementation further down):
        MockDataProvider provider = new MyProvider();
        MockConnection connection = new MockConnection(provider);
        // Pass the mock connection to a jOOQ DSLContext:
        create = DSL.using(connection, SQLDialect.MYSQL);
    }

    @Test
    public void testUsers(){
        Result<UsersRecord> result = create.selectFrom(USERS).where(USERS.USERNAME.equal("863052317@qq.com")).fetch();
        for(UsersRecord u:result){
            System.out.println(u.getUsername());
        }
    }
}
