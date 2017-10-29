package top.zbeboy.isy.test

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockConnection
import org.junit.Before
import org.junit.Test
import top.zbeboy.isy.domain.Tables.USERS
import top.zbeboy.isy.domain.tables.records.UsersRecord

/**
 * Created by zbeboy 2017-10-29 .
 **/
class TestData {

    private var create: DSLContext? = null

    @Before
    fun init() {
        // Initialise your data provider (implementation further down):
        val provider = MyProvider()
        val connection = MockConnection(provider)
        // Pass the mock connection to a jOOQ DSLContext:
        create = DSL.using(connection, SQLDialect.MYSQL)
    }

    @Test
    fun testUsers() {
        val result = create?.selectFrom<UsersRecord>(USERS)?.where(USERS.USERNAME.equal("863052317@qq.com"))?.fetch()
        for (u in result!!) {
            println(u.getUsername())
        }
    }
}