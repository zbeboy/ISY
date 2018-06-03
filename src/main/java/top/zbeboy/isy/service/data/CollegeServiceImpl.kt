package top.zbeboy.isy.service.data

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.COLLEGE
import top.zbeboy.isy.domain.Tables.SCHOOL
import top.zbeboy.isy.domain.tables.daos.CollegeDao
import top.zbeboy.isy.domain.tables.pojos.College
import top.zbeboy.isy.domain.tables.records.CollegeRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.college.CollegeBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-01 .
 **/
@Service("collegeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class CollegeServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<CollegeBean>(), CollegeService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var collegeDao: CollegeDao

    override fun findBySchoolIdAndIsDel(schoolId: Int, b: Byte?): Result<CollegeRecord> {
        return create.selectFrom<CollegeRecord>(COLLEGE)
                .where(COLLEGE.COLLEGE_IS_DEL.eq(b).and(COLLEGE.SCHOOL_ID.eq(schoolId)))
                .fetch()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<CollegeBean>): Result<Record> {
        val a = searchCondition(dataTablesUtils)
        return if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.select()
                    .from(COLLEGE)
                    .join(SCHOOL)
                    .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
            sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(COLLEGE)
                    .join(SCHOOL)
                    .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                    .where(a)
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        }
    }

    override fun countAll(): Int {
        return statisticsAll(create, COLLEGE)
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<CollegeBean>): Int {
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        count = if (ObjectUtils.isEmpty(a)) {
            create.selectCount()
                    .from(COLLEGE).fetchOne()
        } else {
            create.selectCount()
                    .from(COLLEGE)
                    .join(SCHOOL)
                    .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                    .where(a).fetchOne()
        }
        return count.value1()
    }

    override fun findByCollegeNameAndSchoolId(collegeName: String, schoolId: Int): Result<CollegeRecord> {
        return create.selectFrom<CollegeRecord>(COLLEGE)
                .where(COLLEGE.COLLEGE_NAME.eq(collegeName).and(COLLEGE.SCHOOL_ID.eq(schoolId)))
                .fetch()
    }

    override fun findByCollegeCode(collegeCode: String): Result<CollegeRecord> {
        return create.selectFrom<CollegeRecord>(COLLEGE)
                .where(COLLEGE.COLLEGE_CODE.eq(collegeCode))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(college: College) {
        collegeDao.insert(college)
    }

    override fun update(college: College) {
        collegeDao.update(college)
    }

    override fun updateIsDel(ids: List<Int>, isDel: Byte?) {
        for (id in ids) {
            create.update<CollegeRecord>(COLLEGE).set<Byte>(COLLEGE.COLLEGE_IS_DEL, isDel).where(COLLEGE.COLLEGE_ID.eq(id)).execute()
        }
    }

    override fun findById(id: Int): College {
        return collegeDao.findById(id)
    }

    override fun findByIdRelation(collegeId: Int): Optional<Record> {
        return create.select()
                .from(COLLEGE)
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(COLLEGE.COLLEGE_ID.eq(collegeId))
                .fetchOptional()
    }

    override fun findByCollegeNameAndSchoolIdNeCollegeId(collegeName: String, collegeId: Int, schoolId: Int): Result<CollegeRecord> {
        return create.selectFrom<CollegeRecord>(COLLEGE)
                .where(COLLEGE.COLLEGE_NAME.eq(collegeName).and(COLLEGE.COLLEGE_ID.ne(collegeId)).and(COLLEGE.SCHOOL_ID.eq(schoolId)))
                .fetch()
    }

    override fun findByCollegeCodeNeCollegeId(collegeCode: String, collegeId: Int): Result<CollegeRecord> {
        return create.selectFrom<CollegeRecord>(COLLEGE)
                .where(COLLEGE.COLLEGE_CODE.eq(collegeCode).and(COLLEGE.COLLEGE_ID.ne(collegeId)))
                .fetch()
    }

    /**
     * 院数据全局搜索条件
     *
     * @param dataTablesUtils datatable工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<CollegeBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val schoolName = StringUtils.trimWhitespace(search!!.getString("schoolName"))
            val collegeName = StringUtils.trimWhitespace(search.getString("collegeName"))
            if (StringUtils.hasLength(schoolName)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(schoolName))
            }

            if (StringUtils.hasLength(collegeName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName))
                } else {
                    a = a!!.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName)))
                }
            }

        }
        return a
    }

    /**
     * 院数据排序
     *
     * @param dataTablesUtils     datatable工具类
     * @param selectConditionStep 条件
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<CollegeBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("college_id".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_ID.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_ID.desc()
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc()
                    sortField[1] = COLLEGE.COLLEGE_ID.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc()
                    sortField[1] = COLLEGE.COLLEGE_ID.desc()
                }
            }

            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc()
                }
            }

            if ("college_code".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_CODE.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_CODE.desc()
                }
            }

            if ("college_address".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_ADDRESS.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_ADDRESS.desc()
                }
            }

            if ("college_is_del".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_IS_DEL.asc()
                    sortField[1] = COLLEGE.COLLEGE_ID.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_IS_DEL.desc()
                    sortField[1] = COLLEGE.COLLEGE_ID.desc()
                }
            }
        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }

}