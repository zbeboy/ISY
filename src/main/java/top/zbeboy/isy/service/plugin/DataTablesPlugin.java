package top.zbeboy.isy.service.plugin;

import org.jooq.*;
import org.springframework.util.ObjectUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by zbeboy on 2016/9/20.
 * datatables 分页插件
 */
public class DataTablesPlugin<T> {

    public static final int CONDITION_TYPE = 0;

    public static final int JOIN_TYPE = 1;

    /*
    排序
     */
    protected SortField<Integer> sortInteger;
    protected SortField<String> sortString;
    protected SortField<Byte> sortByte;
    protected SortField<Date> sortDate;
    protected SortField<Timestamp> sortTimestamp;

    /**
     * 查询全部数据
     *
     * @param dataTablesUtils datatables工具类
     * @param create          jooq create.
     * @param table           jooq table.
     * @return 全部数据
     */
    public Result<Record> dataPagingQueryAll(DataTablesUtils<T> dataTablesUtils, final DSLContext create, TableLike<?> table) {
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record> selectJoinStep = create.select()
                    .from(table);
            sortCondition(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            pagination(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            records = selectJoinStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(table)
                    .where(a);
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }
        return records;
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
    public Result<Record> dataPagingQueryAllWithCondition(DataTablesUtils<T> dataTablesUtils, final DSLContext create, TableLike<?> table, Condition extraCondition) {
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(table)
                    .where(extraCondition);
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(table)
                    .where(extraCondition.and(a));
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }
        return records;
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
    public Result<Record> dataPagingQueryAllWithConditionNoPage(DataTablesUtils<T> dataTablesUtils, final DSLContext create, TableLike<?> table, Condition extraCondition) {
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(table)
                    .where(extraCondition);
            records = selectConditionStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(table)
                    .where(extraCondition.and(a));
            records = selectConditionStep.fetch();
        }
        return records;
    }

    /**
     * 统计全部
     *
     * @param create jooq create.
     * @param table  jooq table.
     * @return 统计
     */
    public int statisticsAll(final DSLContext create, TableLike<?> table) {
        Record1<Integer> count = create.selectCount()
                .from(table)
                .fetchOne();
        return count.value1();
    }

    /**
     * 统计全部 with 额外条件
     *
     * @param create         jooq create.
     * @param table          jooq table.
     * @param extraCondition 额外条件
     * @return 统计
     */
    public int statisticsAllWithCondition(final DSLContext create, TableLike<?> table, Condition extraCondition) {
        Record1<Integer> count = create.selectCount()
                .from(table)
                .where(extraCondition)
                .fetchOne();
        return count.value1();
    }

    /**
     * 根据条件统计
     *
     * @param dataTablesUtils datatables工具类
     * @param create          jooq create.
     * @param table           jooq table.
     * @return 统计
     */
    public int statisticsWithCondition(DataTablesUtils<T> dataTablesUtils, final DSLContext create, TableLike<?> table) {
        Record1<Integer> count;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(table);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(table)
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
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
    public int statisticsWithCondition(DataTablesUtils<T> dataTablesUtils, final DSLContext create, TableLike<?> table, Condition extraCondition) {
        Record1<Integer> count;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(table)
                    .where(extraCondition);
            count = selectConditionStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(table)
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    /**
     * 查询条件，需要自行覆盖
     *
     * @param dataTablesUtils datatables工具类
     * @return 查询条件
     */
    public Condition searchCondition(DataTablesUtils<T> dataTablesUtils) {
        return null;
    }

    /**
     * 排序方式，需要自行覆盖
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件1
     * @param selectJoinStep      条件2
     * @param type                类型
     */
    public void sortCondition(DataTablesUtils<T> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {

    }

    /**
     * set sort param to null.
     * warning: if you going to use sortToFinish method . please before clean with this method , it void you sort be clone.
     */
    protected void cleanSortParam() {
        sortInteger = null;
        sortString = null;
        sortByte = null;
        sortDate = null;
        sortTimestamp = null;
    }

    /**
     * 排序辅助,调用此方法前请先调用cleanSortParam以避免对象污染所造成的排序混乱
     *
     * @param selectConditionStep 条件1
     * @param selectJoinStep      条件2
     * @param type                类型
     */
    public void sortToFinish(SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        if (!ObjectUtils.isEmpty(sortInteger)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(sortInteger);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(sortInteger);
            }

        } else if (!ObjectUtils.isEmpty(sortString)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(sortString);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(sortString);
            }
        } else if (!ObjectUtils.isEmpty(sortByte)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(sortByte);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(sortByte);
            }
        } else if (!ObjectUtils.isEmpty(sortDate)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(sortDate);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(sortDate);
            }
        } else if (!ObjectUtils.isEmpty(sortTimestamp)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(sortTimestamp);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(sortTimestamp);
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
    public void pagination(DataTablesUtils<T> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        int start = dataTablesUtils.getStart();
        int length = dataTablesUtils.getLength();

        if (type == CONDITION_TYPE) {
            selectConditionStep.limit(start, length);
        }

        if (type == JOIN_TYPE) {
            selectJoinStep.limit(start, length);
        }
    }
}
