package top.zbeboy.isy.service.data

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.ScienceDao
import top.zbeboy.isy.domain.tables.pojos.Science
import top.zbeboy.isy.domain.tables.records.ScienceRecord
import top.zbeboy.isy.elastic.repository.OrganizeElasticRepository
import top.zbeboy.isy.service.common.MethodServiceCommon
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.science.ScienceBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-03 .
 **/
@Service("scienceService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class ScienceServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<ScienceBean>(), ScienceService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var scienceDao: ScienceDao

    @Resource
    open lateinit var methodServiceCommon: MethodServiceCommon

    @Resource
    open lateinit var organizeElasticRepository: OrganizeElasticRepository


    override fun findByDepartmentIdAndIsDel(departmentId: Int, b: Byte?): Result<ScienceRecord> {
        return create.selectFrom(SCIENCE)
                .where(SCIENCE.SCIENCE_IS_DEL.eq(b).and(SCIENCE.DEPARTMENT_ID.eq(departmentId)))
                .fetch()
    }

    override fun findByGradeAndDepartmentId(grade: String, departmentId: Int): Result<Record2<String, Int>> {
        return create.selectDistinct(SCIENCE.SCIENCE_NAME, SCIENCE.SCIENCE_ID)
                .from(SCIENCE)
                .join(ORGANIZE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .where(SCIENCE.SCIENCE_IS_DEL.eq(0).and(ORGANIZE.GRADE.eq(grade)).and(SCIENCE.DEPARTMENT_ID.eq(departmentId)))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(science: Science) {
        scienceDao.insert(science)
    }

    override fun update(science: Science) {
        scienceDao.update(science)
        val records = organizeElasticRepository.findByScienceId(science.scienceId!!)
        records.forEach { organizeElastic ->
            organizeElastic.scienceId = science.scienceId
            organizeElastic.scienceName = science.scienceName
            organizeElasticRepository.delete(organizeElastic)
            organizeElasticRepository.save(organizeElastic)
        }
    }

    override fun updateIsDel(ids: List<Int>, isDel: Byte?) {
        for (id in ids) {
            create.update(SCIENCE).set(SCIENCE.SCIENCE_IS_DEL, isDel).where(SCIENCE.SCIENCE_ID.eq(id)).execute()
        }
    }

    override fun findById(id: Int): Science {
        return scienceDao.findById(id)
    }

    override fun findByIdRelation(id: Int): Optional<Record> {
        return create.select()
                .from(SCIENCE)
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(SCIENCE.SCIENCE_ID.eq(id))
                .fetchOptional()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<ScienceBean>): Result<Record> {
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildScienceCondition()
        return if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectJoinStep = create.select()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
                pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
                selectJoinStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(roleCondition)
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                selectConditionStep.fetch()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.select()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a)
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                selectConditionStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(roleCondition!!.and(a))
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                selectConditionStep.fetch()
            }
        }
    }

    override fun countAll(): Int {
        val roleCondition = buildScienceCondition()
        return if (ObjectUtils.isEmpty(roleCondition)) {
            statisticsAll(create, SCIENCE)
        } else {
            create.selectCount()
                    .from(SCIENCE)
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(roleCondition)
                    .fetchOne().value1()
        }
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<ScienceBean>): Int {
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildScienceCondition()
        count = if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectJoinStep = create.selectCount()
                        .from(SCIENCE)
                selectJoinStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(roleCondition)
                selectConditionStep.fetchOne()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a)
                selectConditionStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(roleCondition!!.and(a))
                selectConditionStep.fetchOne()
            }
        }
        return count.value1()
    }

    override fun findByScienceNameAndDepartmentId(scienceName: String, departmentId: Int): Result<ScienceRecord> {
        return create.selectFrom(SCIENCE)
                .where(SCIENCE.SCIENCE_NAME.eq(scienceName).and(SCIENCE.DEPARTMENT_ID.eq(departmentId)))
                .fetch()
    }

    override fun findByScienceCode(scienceCode: String): Result<ScienceRecord> {
        return create.selectFrom(SCIENCE)
                .where(SCIENCE.SCIENCE_CODE.eq(scienceCode))
                .fetch()
    }

    override fun findByScienceNameAndDepartmentIdNeScienceId(scienceName: String, scienceId: Int, departmentId: Int): Result<ScienceRecord> {
        return create.selectFrom(SCIENCE)
                .where(SCIENCE.SCIENCE_NAME.eq(scienceName).and(SCIENCE.DEPARTMENT_ID.eq(departmentId)).and(SCIENCE.SCIENCE_ID.ne(scienceId)))
                .fetch()
    }

    override fun findByScienceCodeNeScienceId(scienceCode: String, scienceId: Int): Result<ScienceRecord> {
        return create.selectFrom(SCIENCE)
                .where(SCIENCE.SCIENCE_CODE.eq(scienceCode).and(SCIENCE.SCIENCE_ID.ne(scienceId)))
                .fetch()
    }

    /**
     * 专业数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<ScienceBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val schoolName = StringUtils.trimWhitespace(search!!.getString("schoolName"))
            val collegeName = StringUtils.trimWhitespace(search.getString("collegeName"))
            val departmentName = StringUtils.trimWhitespace(search.getString("departmentName"))
            val scienceName = StringUtils.trimWhitespace(search.getString("scienceName"))
            if (StringUtils.hasLength(schoolName)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(schoolName))
            }

            if (StringUtils.hasLength(collegeName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName))
                } else {
                    a!!.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName)))
                }
            }

            if (StringUtils.hasLength(departmentName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(departmentName))
                } else {
                    a!!.and(DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(departmentName)))
                }
            }

            if (StringUtils.hasLength(scienceName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    SCIENCE.SCIENCE_NAME.like(SQLQueryUtils.likeAllParam(scienceName))
                } else {
                    a!!.and(SCIENCE.SCIENCE_NAME.like(SQLQueryUtils.likeAllParam(scienceName)))
                }
            }

        }
        return a
    }

    /**
     * 专业数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<ScienceBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("science_id".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_ID.asc()
                } else {
                    sortField[0] = SCIENCE.SCIENCE_ID.desc()
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc()
                    sortField[1] = SCIENCE.SCIENCE_ID.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc()
                    sortField[1] = SCIENCE.SCIENCE_ID.desc()
                }
            }

            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc()
                    sortField[1] = SCIENCE.SCIENCE_ID.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc()
                    sortField[1] = SCIENCE.SCIENCE_ID.desc()
                }
            }

            if ("department_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.asc()
                    sortField[1] = SCIENCE.SCIENCE_ID.asc()
                } else {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.desc()
                    sortField[1] = SCIENCE.SCIENCE_ID.desc()
                }
            }

            if ("science_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_NAME.asc()
                } else {
                    sortField[0] = SCIENCE.SCIENCE_NAME.desc()
                }
            }

            if ("science_code".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_CODE.asc()
                } else {
                    sortField[0] = SCIENCE.SCIENCE_CODE.desc()
                }
            }

            if ("science_is_del".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_IS_DEL.asc()
                    sortField[1] = SCIENCE.SCIENCE_ID.asc()
                } else {
                    sortField[0] = SCIENCE.SCIENCE_IS_DEL.desc()
                    sortField[1] = SCIENCE.SCIENCE_ID.desc()
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }

    /**
     * 构建该角色查询条件
     */
    private fun buildScienceCondition(): Condition? {
        return methodServiceCommon.buildDepartmentCondition()
    }
}