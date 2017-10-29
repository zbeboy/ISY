package top.zbeboy.isy.test

import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockDataProvider
import org.jooq.tools.jdbc.MockExecuteContext
import org.jooq.tools.jdbc.MockResult
import top.zbeboy.isy.domain.tables.records.UsersRecord
import java.sql.SQLException

import top.zbeboy.isy.domain.Tables.USERS

class MyProvider : MockDataProvider {
    override fun execute(ctx: MockExecuteContext?): Array<MockResult?> {
        // You might need a DSLContext to create org.jooq.Result and org.jooq.Record objects
        val create = DSL.using(SQLDialect.MYSQL)
        val mock = arrayOfNulls<MockResult>(1)
        // The execute context contains SQL string(s), bind values, and other meta-data
        val sql = ctx?.sql()
        // Exceptions are propagated through the JDBC and jOOQ APIs
        if (sql!!.toUpperCase().startsWith("DROP")) {
            throw SQLException("Statement not supported: " + sql)
        } else if (sql.toUpperCase().startsWith("SELECT")) {
            // Always return one author record
            /* Result<AuthorRecord> result = create.newResult(AUTHOR);
            result.add(create.newRecord(AUTHOR));
            result.get(0).setValue(AUTHOR.ID, 1);
            result.get(0).setValue(AUTHOR.LAST_NAME, "Orwell");
            mock[0] = new MockResult(1, result);*/
            val result = create.newResult<UsersRecord>(USERS)
            result.add(create.newRecord<UsersRecord>(USERS))
            result.get(0).setValue(USERS.USERNAME, "test@mail.com")
            result.get(0).setValue(USERS.PASSWORD, "$2a$10\$HKXHRhnhlC1aZQ4hukD0S.zYep/T5A7FULBo7S2UrJsqQCThUxdo2")
            result.get(0).setValue(USERS.ENABLED, 1)
            result.get(0).setValue(USERS.USERS_TYPE_ID, 3)
            result.get(0).setValue(USERS.MOBILE, "13987614000")
            mock[0] = MockResult(1, result)
        } else if (ctx.batch()) {

        }// You can detect batch statements easily
        // You decide, whether any given statement returns results, and how many
        return mock
    }

}