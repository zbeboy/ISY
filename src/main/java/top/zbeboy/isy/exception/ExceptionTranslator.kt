package top.zbeboy.isy.exception

import org.jooq.ExecuteContext
import org.jooq.impl.DefaultExecuteListener
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator

/**
 * Created by zbeboy 2017-11-02 .
 **/
class ExceptionTranslator : DefaultExecuteListener() {

    override fun exception(ctx: ExecuteContext?) {

        // [#4391] Translate only SQLExceptions
        if (ctx!!.sqlException() != null) {
            val dialect = ctx.dialect()
            val translator = if (dialect != null)
                SQLErrorCodeSQLExceptionTranslator(dialect.thirdParty().springDbName())
            else
                SQLStateSQLExceptionTranslator()

            ctx.exception(translator.translate("jOOQ", ctx.sql(), ctx.sqlException()))
        }
    }
}