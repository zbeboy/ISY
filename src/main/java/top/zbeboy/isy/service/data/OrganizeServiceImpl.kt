package top.zbeboy.isy.service.data

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.OrganizeDao
import top.zbeboy.isy.domain.tables.pojos.Organize
import top.zbeboy.isy.domain.tables.records.OrganizeRecord
import top.zbeboy.isy.elastic.pojo.OrganizeElastic
import top.zbeboy.isy.elastic.repository.OrganizeElasticRepository
import top.zbeboy.isy.service.common.MethodServiceCommon
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.organize.OrganizeBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-03 .
 **/
@Service("organizeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class OrganizeServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<OrganizeBean>(), OrganizeService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var organizeDao: OrganizeDao

    @Resource
    open lateinit var methodServiceCommon: MethodServiceCommon

    @Resource
    open lateinit var organizeElasticRepository: OrganizeElasticRepository

    override fun findByScienceIdAndDistinctGradeAndIsDel(scienceId: Int, b: Byte?): Result<Record1<String>> {
        return create.selectDistinct<String>(ORGANIZE.GRADE)
                .from(ORGANIZE)
                .where(ORGANIZE.SCIENCE_ID.eq(scienceId).and(ORGANIZE.ORGANIZE_IS_DEL.eq(b)))
                .orderBy(ORGANIZE.ORGANIZE_ID.desc())
                .limit(0, 6)
                .fetch()
    }

    override fun findInScienceIdsAndGradeAndIsDel(scienceIds: List<Int>, grade: String, b: Byte?): Result<OrganizeRecord> {
        return create.selectFrom<OrganizeRecord>(ORGANIZE)
                .where(ORGANIZE.SCIENCE_ID.`in`(scienceIds).and(ORGANIZE.GRADE.eq(grade)).and(ORGANIZE.ORGANIZE_IS_DEL.eq(b)))
                .fetch()
    }

    override fun findByScienceIdAndGradeAndIsDel(scienceId: Int, grade: String, b: Byte?): Result<OrganizeRecord> {
        return create.selectFrom<OrganizeRecord>(ORGANIZE)
                .where(ORGANIZE.SCIENCE_ID.eq(scienceId).and(ORGANIZE.GRADE.eq(grade)).and(ORGANIZE.ORGANIZE_IS_DEL.eq(b)))
                .fetch()
    }

    override fun findByScienceId(scienceId: Int): List<Organize> {
        return organizeDao.fetchByScienceId(scienceId)
    }

    override fun findByDepartmentIdAndDistinctGrade(departmentId: Int): Result<Record1<String>> {
        return create.selectDistinct<String>(ORGANIZE.GRADE)
                .from(ORGANIZE)
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .where(DEPARTMENT.DEPARTMENT_ID.eq(departmentId).and(ORGANIZE.ORGANIZE_IS_DEL.eq(0)))
                .orderBy(ORGANIZE.GRADE.desc())
                .limit(0, 6)
                .fetch()
    }

    override fun findByOrganizeNameAndScienceIdNeOrganizeId(organizeName: String, organizeId: Int, scienceId: Int): Result<OrganizeRecord> {
        return create.selectFrom<OrganizeRecord>(ORGANIZE)
                .where(ORGANIZE.ORGANIZE_NAME.eq(organizeName).and(ORGANIZE.SCIENCE_ID.eq(scienceId)).and(ORGANIZE.ORGANIZE_ID.ne(organizeId)))
                .fetch()
    }

    override fun findByGradeAndScienceId(grade: String, scienceId: Int): Result<OrganizeRecord> {
        return create.selectFrom<OrganizeRecord>(ORGANIZE)
                .where(ORGANIZE.ORGANIZE_IS_DEL.eq(0).and(ORGANIZE.GRADE.eq(grade)).and(ORGANIZE.SCIENCE_ID.eq(scienceId)))
                .fetch()
    }

    override fun findByGradeAndScienceIdNotIsDel(grade: String, scienceId: Int): Result<OrganizeRecord> {
        return create.selectFrom<OrganizeRecord>(ORGANIZE)
                .where(ORGANIZE.GRADE.eq(grade).and(ORGANIZE.SCIENCE_ID.eq(scienceId)))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(organizeElastic: OrganizeElastic) {
        val record = create.insertInto<OrganizeRecord>(ORGANIZE)
                .set<String>(ORGANIZE.ORGANIZE_NAME, organizeElastic.organizeName)
                .set<Byte>(ORGANIZE.ORGANIZE_IS_DEL, organizeElastic.organizeIsDel)
                .set<Int>(ORGANIZE.SCIENCE_ID, organizeElastic.scienceId)
                .set<String>(ORGANIZE.GRADE, organizeElastic.grade)
                .returning(ORGANIZE.ORGANIZE_ID)
                .fetchOne()
        organizeElastic.setOrganizeId(record.organizeId)
        organizeElasticRepository.save(organizeElastic)
    }

    override fun update(organize: Organize) {
        organizeDao.update(organize)
        val organizeData = organizeElasticRepository.findById(organize.organizeId!!.toString() + "")
        if(organizeData.isPresent){
            val organizeElastic = organizeData.get()
            organizeElastic.organizeIsDel = organize.organizeIsDel
            organizeElastic.organizeName = organize.organizeName
            organizeElastic.scienceId = organize.scienceId
            organizeElastic.grade = organize.grade
            organizeElasticRepository.delete(organizeElastic)
            organizeElasticRepository.save(organizeElastic)
        }

    }

    override fun updateIsDel(ids: List<Int>, isDel: Byte?) {
        ids.forEach { id ->
            create.update<OrganizeRecord>(ORGANIZE).set<Byte>(ORGANIZE.ORGANIZE_IS_DEL, isDel).where(ORGANIZE.ORGANIZE_ID.eq(id)).execute()
            val organizeData = organizeElasticRepository.findById(id.toString() + "")
            if(organizeData.isPresent){
                val organizeElastic = organizeData.get()
                organizeElastic.organizeIsDel = isDel
                organizeElasticRepository.delete(organizeElastic)
                organizeElasticRepository.save(organizeElastic)
            }
        }
    }

    override fun findByIdRelation(id: Int): Optional<Record> {
        return create.select()
                .from(ORGANIZE)
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(ORGANIZE.ORGANIZE_ID.eq(id))
                .fetchOptional()
    }

    override fun findById(id: Int): Organize {
        return organizeDao.findById(id)
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<OrganizeBean>): Result<Record> {
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildOrganizeCondition()
        return if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectJoinStep = create.select()
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
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
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
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
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
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
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
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
        val roleCondition = buildOrganizeCondition()
        return if (ObjectUtils.isEmpty(roleCondition)) {
            statisticsAll(create, ORGANIZE)
        } else {
            create.selectCount()
                    .from(ORGANIZE)
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(roleCondition)
                    .fetchOne().value1()
        }
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<OrganizeBean>): Int {
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildOrganizeCondition()
        count = if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                create.selectCount()
                        .from(ORGANIZE).fetchOne()
            } else {
                create.selectCount()
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(roleCondition).fetchOne()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                create.selectCount()
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a).fetchOne()
            } else {
                create.selectCount()
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(roleCondition!!.and(a)).fetchOne()
            }
        }
        return count.value1()
    }

    override fun findByOrganizeNameAndScienceId(organizeName: String, scienceId: Int): Result<OrganizeRecord> {
        return create.selectFrom<OrganizeRecord>(ORGANIZE)
                .where(ORGANIZE.ORGANIZE_NAME.eq(organizeName).and(ORGANIZE.SCIENCE_ID.eq(scienceId)))
                .fetch()
    }

    /**
     * 班级数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<OrganizeBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val schoolName = StringUtils.trimWhitespace(search!!.getString("schoolName"))
            val collegeName = StringUtils.trimWhitespace(search.getString("collegeName"))
            val departmentName = StringUtils.trimWhitespace(search.getString("departmentName"))
            val scienceName = StringUtils.trimWhitespace(search.getString("scienceName"))
            val grade = StringUtils.trimWhitespace(search.getString("grade"))
            val organizeName = StringUtils.trimWhitespace(search.getString("organizeName"))
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

            if (StringUtils.hasLength(grade)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    ORGANIZE.GRADE.like(SQLQueryUtils.likeAllParam(grade))
                } else {
                    a!!.and(ORGANIZE.GRADE.like(SQLQueryUtils.likeAllParam(grade)))
                }
            }

            if (StringUtils.hasLength(organizeName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organizeName))
                } else {
                    a!!.and(ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organizeName)))
                }
            }

        }
        return a
    }

    /**
     * 班级数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<OrganizeBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("organize_id".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_ID.asc()
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_ID.desc()
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc()
                }
            }

            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc()
                }
            }

            if ("department_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.asc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc()
                } else {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.desc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc()
                }
            }

            if ("science_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_NAME.asc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc()
                } else {
                    sortField[0] = SCIENCE.SCIENCE_NAME.desc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc()
                }
            }

            if ("grade".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ORGANIZE.GRADE.asc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc()
                } else {
                    sortField[0] = ORGANIZE.GRADE.desc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc()
                }
            }

            if ("organize_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.asc()
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc()
                }
            }

            if ("organize_is_del".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_IS_DEL.asc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc()
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_IS_DEL.desc()
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc()
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }

    /**
     * 构建该角色查询条件
     */
    private fun buildOrganizeCondition(): Condition? {
        return methodServiceCommon.buildDepartmentCondition()
    }

}