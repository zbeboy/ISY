package top.zbeboy.isy.test;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import top.zbeboy.isy.domain.tables.records.UsersRecord;

import java.sql.SQLException;

import static top.zbeboy.isy.domain.Tables.USERS;

/**
 * Created by zbeboy on 2016/11/7.
 */
public class MyProvider implements MockDataProvider {
    @Override
    public MockResult[] execute(MockExecuteContext ctx) throws SQLException {
        // You might need a DSLContext to create org.jooq.Result and org.jooq.Record objects
        DSLContext create = DSL.using(SQLDialect.MYSQL);
        MockResult[] mock = new MockResult[1];
        // The execute context contains SQL string(s), bind values, and other meta-data
        String sql = ctx.sql();
        // Exceptions are propagated through the JDBC and jOOQ APIs
        if (sql.toUpperCase().startsWith("DROP")) {
            throw new SQLException("Statement not supported: " + sql);
        }
        // You decide, whether any given statement returns results, and how many
        else if (sql.toUpperCase().startsWith("SELECT")) {
            // Always return one author record
           /* Result<AuthorRecord> result = create.newResult(AUTHOR);
            result.add(create.newRecord(AUTHOR));
            result.get(0).setValue(AUTHOR.ID, 1);
            result.get(0).setValue(AUTHOR.LAST_NAME, "Orwell");
            mock[0] = new MockResult(1, result);*/
            Byte enabled = 1;
            Result<UsersRecord> result = create.newResult(USERS);
            result.add(create.newRecord(USERS));
            result.get(0).setValue(USERS.USERNAME, "test@mail.com");
            result.get(0).setValue(USERS.PASSWORD, "$2a$10$HKXHRhnhlC1aZQ4hukD0S.zYep/T5A7FULBo7S2UrJsqQCThUxdo2");
            result.get(0).setValue(USERS.ENABLED, enabled);
            result.get(0).setValue(USERS.USERS_TYPE_ID, 3);
            result.get(0).setValue(USERS.MOBILE, "13987614709");
            mock[0] = new MockResult(1, result);
        }
        // You can detect batch statements easily
        else if (ctx.batch()) {

        }
        return mock;
    }
}
