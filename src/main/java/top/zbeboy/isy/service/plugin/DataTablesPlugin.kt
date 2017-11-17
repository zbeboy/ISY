package top.zbeboy.isy.service.plugin

import org.jooq.*
import org.springframework.util.ObjectUtils
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-11-17 .
 **/
open class DataTablesPlugin<T> {

    companion object {
        @JvmField
        val CONDITION_TYPE = 0

        @JvmField
        val JOIN_TYPE = 1
    }

    /**
     * 查询全部数据
     *
     * @param dataTablesUtils datatables工具类
     * @param create          jooq create.
     * @param table           jooq table.
     * @return 全部数据
     */
    open fun dataPagingQueryAll(dataTablesUtils: DataTablesUtils<T>, create: DSLContext, table: TableLike<*>): Result<Record> {
        val records: Result<Record>
        val a = searchCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.select()
                    .from(table)
            sortCondition(dataTablesUtils, null, selectJoinStep, JOIN_TYPE)
            pagination(dataTablesUtils, null, selectJoinStep, JOIN_TYPE)
            records = selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(table)
                    .where(a)
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE)
            records = selectConditionStep.fetch()
        }
        return records
    }

    /**
     * 查询全部数据 with 额外条件
     *
     * @param dataTablesUtils datatables工具类
     * @param create          jooq create.
     * @param table           jooq table.
     * @param extraCondition  额外条件
     * @return 全部数据
     */
    open fun dataPagingQueryAllWithCondition(dataTablesUtils: DataTablesUtils<T>, create: DSLContext, table: TableLike<*>, extraCondition: Condition): Result<Record> {
        val records: Result<Record>
        val a = searchCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.select()
                    .from(table)
                    .where(extraCondition)
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE)
            records = selectConditionStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(table)
                    .where(extraCondition.and(a))
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE)
            records = selectConditionStep.fetch()
        }
        return records
    }

    /**
     * 查询全部数据 with 额外条件
     *
     * @param dataTablesUtils datatables工具类
     * @param create          jooq create.
     * @param table           jooq table.
     * @param extraCondition  额外条件
     * @return 全部数据
     */
    open fun dataPagingQueryAllWithConditionNoPage(dataTablesUtils: DataTablesUtils<T>, create: DSLContext, table: TableLike<*>, extraCondition: Condition): Result<Record> {
        val records: Result<Record>
        val a = searchCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.select()
                    .from(table)
                    .where(extraCondition)
            records = selectConditionStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(table)
                    .where(extraCondition.and(a))
            records = selectConditionStep.fetch()
        }
        return records
    }

    /**
     * 统计全部
     *
     * @param create jooq create.
     * @param table  jooq table.
     * @return 统计
     */
    open fun statisticsAll(create: DSLContext, table: TableLike<*>): Int {
        val count = create.selectCount()
                .from(table)
                .fetchOne()
        return count.value1()
    }

    /**
     * 统计全部 with 额外条件
     *
     * @param create         jooq create.
     * @param table          jooq table.
     * @param extraCondition 额外条件
     * @return 统计
     */
    open fun statisticsAllWithCondition(create: DSLContext, table: TableLike<*>, extraCondition: Condition): Int {
        val count = create.selectCount()
                .from(table)
                .where(extraCondition)
                .fetchOne()
        return count.value1()
    }

    /**
     * 根据条件统计
     *
     * @param dataTablesUtils datatables工具类
     * @param create          jooq create.
     * @param table           jooq table.
     * @return 统计
     */
    open fun statisticsWithCondition(dataTablesUtils: DataTablesUtils<T>, create: DSLContext, table: TableLike<*>): Int {
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(table)
            count = selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(table)
                    .where(a)
            count = selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    /**
     * 根据条件统计
     *
     * @param dataTablesUtils datatables工具类
     * @param create          jooq create.
     * @param table           jooq table.
     * @param extraCondition  额外条件
     * @return 统计
     */
    open fun statisticsWithCondition(dataTablesUtils: DataTablesUtils<T>, create: DSLContext, table: TableLike<*>, extraCondition: Condition): Int {
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.selectCount()
                    .from(table)
                    .where(extraCondition)
            count = selectConditionStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(table)
                    .where(a)
            count = selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    /**
     * 查询条件，需要自行覆盖
     *
     * @param dataTablesUtils datatables工具类
     * @return 查询条件
     */
    open fun searchCondition(dataTablesUtils: DataTablesUtils<T>): Condition? {
        return null
    }

    /**
     * 排序方式，需要自行覆盖
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件1
     * @param selectJoinStep      条件2
     * @param type                类型
     */
    open fun sortCondition(dataTablesUtils: DataTablesUtils<T>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {}

    /**
     * 排序辅助,调用此方法前请先调用cleanSortParam以避免对象污染所造成的排序混乱
     *
     * @param selectConditionStep 条件1
     * @param selectJoinStep      条件2
     * @param type                类型
     */
    open fun sortToFinish(selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int, vararg sortField: SortField<*>?) {
        if (!ObjectUtils.isEmpty(sortField)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep!!.orderBy(*sortField)
            }

            if (type == JOIN_TYPE) {
                selectJoinStep!!.orderBy(*sortField)
            }
        }
    }

    /**
     * 分页方式
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件1
     * @param selectJoinStep      条件2
     * @param type                类型
     */
    fun pagination(dataTablesUtils: DataTablesUtils<T>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val start = dataTablesUtils.start
        val length = dataTablesUtils.length

        if (type == CONDITION_TYPE) {
            selectConditionStep!!.limit(start, length)
        }

        if (type == JOIN_TYPE) {
            selectJoinStep!!.limit(start, length)
        }
    }
}