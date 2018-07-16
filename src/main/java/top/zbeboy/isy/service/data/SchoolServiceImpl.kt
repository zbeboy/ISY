package top.zbeboy.isy.service.data

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.SCHOOL
import top.zbeboy.isy.domain.tables.daos.SchoolDao
import top.zbeboy.isy.domain.tables.pojos.School
import top.zbeboy.isy.domain.tables.records.SchoolRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-01 .
 **/
@Service("schoolService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class SchoolServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<School>(), SchoolService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var schoolDao: SchoolDao

    override fun findByIsDel(b: Byte?): Result<SchoolRecord> {
        return create.selectFrom<SchoolRecord>(SCHOOL)
                .where(SCHOOL.SCHOOL_IS_DEL.eq(b))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(school: School) {
        schoolDao.insert(school)
    }

    override fun update(school: School) {
        schoolDao.update(school)
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<School>): Result<Record> {
        return dataPagingQueryAll(dataTablesUtils, create, SCHOOL)
    }

    override fun countAll(): Int {
        return statisticsAll(create, SCHOOL)
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<School>): Int {
        return statisticsWithCondition(dataTablesUtils, create, SCHOOL)
    }

    override fun findBySchoolName(schoolName: String): List<School> {
        return schoolDao.fetchBySchoolName(schoolName)
    }

    override fun findBySchoolNameNeSchoolId(schoolName: String, schoolId: Int): Result<SchoolRecord> {
        return create.selectFrom<SchoolRecord>(SCHOOL)
                .where(SCHOOL.SCHOOL_NAME.eq(schoolName).and(SCHOOL.SCHOOL_ID.ne(schoolId)))
                .fetch()
    }

    override fun updateIsDel(ids: List<Int>, isDel: Byte?) {
        for (id in ids) {
            create.update<SchoolRecord>(SCHOOL).set<Byte>(SCHOOL.SCHOOL_IS_DEL, isDel).where(SCHOOL.SCHOOL_ID.eq(id)).execute()
        }
    }

    override fun findById(id: Int): School {
        return schoolDao.findById(id)
    }

    /**
     * 学校数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<School>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val schoolName = StringUtils.trimWhitespace(search!!.getString("schoolName"))
            if (StringUtils.hasLength(schoolName)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(schoolName))
            }
        }
        return a
    }

    /**
     * 学校数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<School>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("school_id".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_ID.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_ID.desc()
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc()
                    sortField[1] = SCHOOL.SCHOOL_ID.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc()
                    sortField[1] = SCHOOL.SCHOOL_ID.desc()
                }
            }

            if ("school_is_del".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_IS_DEL.asc()
                    sortField[1] = SCHOOL.SCHOOL_ID.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_IS_DEL.desc()
                    sortField[1] = SCHOOL.SCHOOL_ID.desc()
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }

}