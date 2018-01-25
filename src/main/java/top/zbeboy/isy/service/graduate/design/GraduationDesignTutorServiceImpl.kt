package top.zbeboy.isy.service.graduate.design

import org.apache.commons.lang3.math.NumberUtils
import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.GraduationDesignTutorDao
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTutor
import top.zbeboy.isy.domain.tables.records.GraduationDesignTutorRecord
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-19 .
 **/
@Service("graduationDesignTutorService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignTutorServiceImpl @Autowired constructor(dslContext: DSLContext) : GraduationDesignTutorService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var graduationDesignTutorDao: GraduationDesignTutorDao


    override fun findById(id: String): GraduationDesignTutor {
        return graduationDesignTutorDao.findById(id)
    }

    override fun findByStudentIdAndGraduationDesignReleaseIdRelationForStaff(studentId: Int, graduationDesignReleaseId: String): Optional<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetchOptional()
    }

    override fun findByStudentIdAndGraduationDesignReleaseIdRelation(studentId: Int, graduationDesignReleaseId: String): Optional<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetchOptional()
    }

    override fun findByStaffIdAndGraduationDesignReleaseIdRelationForStudent(staffId: Int, graduationDesignReleaseId: String): Result<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STUDENT)
                .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .where(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(staffId)
                        .and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetch()
    }

    override fun findByGraduationDesignTeacherIdRelationForStudent(graduationDesignTeacherId: String): Result<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(STUDENT)
                .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .where(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId))
                .fetch()
    }

    override fun countNotFillStudent(graduationDesignReleaseBean: GraduationDesignReleaseBean): Int {
        val select = create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseBean.graduationDesignReleaseId).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID)))
        return create.selectCount()
                .from(STUDENT)
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .where(SCIENCE.SCIENCE_ID.eq(graduationDesignReleaseBean.scienceId).and(ORGANIZE.GRADE.eq(graduationDesignReleaseBean.allowGrade))
                        .andNotExists(select))
                .fetchOne().value1()
    }

    override fun countFillStudent(graduationDesignReleaseBean: GraduationDesignReleaseBean): Int {
        return create.selectCount()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseBean.graduationDesignReleaseId))
                .fetchOne().value1()
    }

    override fun deleteByGraduationDesignTeacherId(graduationDesignTeacherId: String) {
        create.deleteFrom<GraduationDesignTutorRecord>(GRADUATION_DESIGN_TUTOR)
                .where(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId))
                .execute()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationDesignTutor: GraduationDesignTutor) {
        graduationDesignTutorDao.insert(graduationDesignTutor)
    }

    override fun update(graduationDesignTutor: GraduationDesignTutor) {
        graduationDesignTutorDao.update(graduationDesignTutor)
    }

    override fun deleteByIds(ids: List<String>) {
        graduationDesignTutorDao.deleteById(ids)
    }

    override fun findAllFillByPage(dataTablesUtils: DataTablesUtils<GraduationDesignTutorBean>, condition: GraduationDesignTutorBean): List<GraduationDesignTutorBean> {
        val graduationDesignTutorBeens = ArrayList<GraduationDesignTutorBean>()
        val records: Result<Record>
        val a = searchFillCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TUTOR)
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.graduationDesignReleaseId))
            sortFillCondition(dataTablesUtils, selectConditionStep)
            fillPagination(dataTablesUtils, selectConditionStep)
            records = selectConditionStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TUTOR)
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.graduationDesignReleaseId).and(a))
            sortFillCondition(dataTablesUtils, selectConditionStep)
            fillPagination(dataTablesUtils, selectConditionStep)
            records = selectConditionStep.fetch()
        }
        for (r in records) {
            val graduationDesignTutorBean = GraduationDesignTutorBean()
            graduationDesignTutorBean.graduationDesignTeacherId = r.getValue(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID)
            graduationDesignTutorBean.studentId = r.getValue(GRADUATION_DESIGN_TUTOR.STUDENT_ID)
            graduationDesignTutorBean.graduationDesignTutorId = r.getValue(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID)
            graduationDesignTutorBean.studentName = r.getValue(USERS.REAL_NAME)
            graduationDesignTutorBean.studentNumber = r.getValue(STUDENT.STUDENT_NUMBER)
            graduationDesignTutorBean.staffName = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME)
            graduationDesignTutorBean.organizeName = r.getValue(ORGANIZE.ORGANIZE_NAME)
            graduationDesignTutorBeens.add(graduationDesignTutorBean)
        }
        return graduationDesignTutorBeens
    }

    override fun countAllFill(condition: GraduationDesignTutorBean): Int {
        val count = create.selectCount()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.graduationDesignReleaseId))
                .fetchOne()
        return count.value1()
    }

    override fun countFillByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignTutorBean>, condition: GraduationDesignTutorBean): Int {
        val count: Record1<Int>
        val a = searchFillCondition(dataTablesUtils)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TUTOR)
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.graduationDesignReleaseId))
            selectConditionStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TUTOR)
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.graduationDesignReleaseId).and(a))
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    override fun findAllNotFillByPage(dataTablesUtils: DataTablesUtils<StudentBean>, condition: GraduationDesignRelease): Result<Record> {
        val a = searchNotFillCondition(dataTablesUtils)
        return if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.select()
                    .from(STUDENT)
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(SCIENCE.SCIENCE_ID.eq(condition.scienceId).and(ORGANIZE.GRADE.eq(condition.allowGrade))
                            .andNotExists(selectTutor(condition)))
            sortNotFillCondition(dataTablesUtils, selectConditionStep)
            notFillPagination(dataTablesUtils, selectConditionStep)
            selectConditionStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(STUDENT)
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(SCIENCE.SCIENCE_ID.eq(condition.scienceId).and(ORGANIZE.GRADE.eq(condition.allowGrade))
                            .andNotExists(selectTutor(condition)).and(a))
            sortNotFillCondition(dataTablesUtils, selectConditionStep)
            notFillPagination(dataTablesUtils, selectConditionStep)
            selectConditionStep.fetch()
        }
    }

    override fun countAllNotFill(condition: GraduationDesignRelease): Int {
        return create.selectCount()
                .from(STUDENT)
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .where(SCIENCE.SCIENCE_ID.eq(condition.scienceId).and(ORGANIZE.GRADE.eq(condition.allowGrade))
                        .andNotExists(selectTutor(condition)))
                .fetchOne().value1()
    }

    override fun countNotFillByCondition(dataTablesUtils: DataTablesUtils<StudentBean>, condition: GraduationDesignRelease): Int {
        val count: Record1<Int>
        val a = searchNotFillCondition(dataTablesUtils)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.selectCount()
                    .from(STUDENT)
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .where(SCIENCE.SCIENCE_ID.eq(condition.scienceId).and(ORGANIZE.GRADE.eq(condition.allowGrade))
                            .andNotExists(selectTutor(condition)))
            selectConditionStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(STUDENT)
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(SCIENCE.SCIENCE_ID.eq(condition.scienceId).and(ORGANIZE.GRADE.eq(condition.allowGrade))
                            .andNotExists(selectTutor(condition)).and(a))
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    override fun countByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId: String, staffId: Int): Int {
        return create.selectCount()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(staffId).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetchOne().value1()
    }

    private fun selectTutor(condition: GraduationDesignRelease): Select<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.graduationDesignReleaseId).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID)))
    }

    /**
     * 已填报学生搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    private fun searchFillCondition(dataTablesUtils: DataTablesUtils<GraduationDesignTutorBean>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val studentName = StringUtils.trimWhitespace(search!!.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val organizeName = StringUtils.trimWhitespace(search.getString("organizeName"))
            if (StringUtils.hasLength(studentName)) {
                a = USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName))
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(organizeName)) {
                val organizeId = NumberUtils.toInt(organizeName)
                if (organizeId > 0) {
                    a = if (ObjectUtils.isEmpty(a)) {
                        ORGANIZE.ORGANIZE_ID.eq(organizeId)
                    } else {
                        a!!.and(ORGANIZE.ORGANIZE_ID.eq(organizeId))
                    }
                }
            }
        }
        return a
    }

    /**
     * 未填报学生搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    private fun searchNotFillCondition(dataTablesUtils: DataTablesUtils<StudentBean>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val studentName = StringUtils.trimWhitespace(search!!.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val organizeName = StringUtils.trimWhitespace(search.getString("organizeName"))
            if (StringUtils.hasLength(studentName)) {
                a = USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName))
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(organizeName)) {
                val organizeId = NumberUtils.toInt(organizeName)
                if (organizeId > 0) {
                    a = if (ObjectUtils.isEmpty(a)) {
                        ORGANIZE.ORGANIZE_ID.eq(organizeId)
                    } else {
                        a!!.and(ORGANIZE.ORGANIZE_ID.eq(organizeId))
                    }
                }
            }
        }
        return a
    }

    /**
     * 已填报学生数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    private fun sortFillCondition(dataTablesUtils: DataTablesUtils<GraduationDesignTutorBean>, selectConditionStep: SelectConditionStep<Record>) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.asc()
                } else {
                    sortField[0] = USERS.REAL_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.desc()
                }
            }

            if ("student_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = STUDENT.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = STUDENT.STUDENT_NUMBER.desc()
                }
            }

            if ("organize_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.asc()
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.desc()
                }
            }

            if ("staff_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.desc()
                }
            }
        }

        if (!ObjectUtils.isEmpty(sortField)) {
            selectConditionStep.orderBy(*sortField!!)
        }
    }

    /**
     * 未填报学生数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    private fun sortNotFillCondition(dataTablesUtils: DataTablesUtils<StudentBean>, selectConditionStep: SelectConditionStep<Record>) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc()
                    sortField[1] = STUDENT.STUDENT_ID.asc()
                } else {
                    sortField[0] = USERS.REAL_NAME.desc()
                    sortField[1] = STUDENT.STUDENT_ID.desc()
                }
            }

            if ("student_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = STUDENT.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = STUDENT.STUDENT_NUMBER.desc()
                }
            }

            if ("organize_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.asc()
                    sortField[1] = STUDENT.STUDENT_ID.asc()
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc()
                    sortField[1] = STUDENT.STUDENT_ID.desc()
                }
            }
        }

        if (!ObjectUtils.isEmpty(sortField)) {
            selectConditionStep.orderBy(*sortField!!)
        }
    }

    /**
     * 已填报学生分页
     *
     * @param dataTablesUtils     分页工具
     * @param selectConditionStep 条件
     */
    private fun fillPagination(dataTablesUtils: DataTablesUtils<GraduationDesignTutorBean>, selectConditionStep: SelectConditionStep<Record>) {
        val start = dataTablesUtils.start
        val length = dataTablesUtils.length
        selectConditionStep.limit(start, length)
    }

    /**
     * 未填报学生分页
     *
     * @param dataTablesUtils     分页工具
     * @param selectConditionStep 条件
     */
    private fun notFillPagination(dataTablesUtils: DataTablesUtils<StudentBean>, selectConditionStep: SelectConditionStep<Record>) {
        val start = dataTablesUtils.start
        val length = dataTablesUtils.length
        selectConditionStep.limit(start, length)
    }
}