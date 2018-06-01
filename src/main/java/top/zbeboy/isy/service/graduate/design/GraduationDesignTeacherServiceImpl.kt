package top.zbeboy.isy.service.graduate.design

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.GraduationDesignTeacherDao
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher
import top.zbeboy.isy.domain.tables.records.GraduationDesignTeacherRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-17 .
 **/
@Service("graduationDesignTeacherService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignTeacherServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<GraduationDesignTeacherBean>(), GraduationDesignTeacherService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var graduationDesignTeacherDao: GraduationDesignTeacherDao

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignTeacherBean>, graduationDesignTeacherBean: GraduationDesignTeacherBean): List<GraduationDesignTeacherBean> {
        val graduationDesignTeacherBeens = ArrayList<GraduationDesignTeacherBean>()
        val records: Result<Record>
        val a = searchCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.graduationDesignReleaseId))
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            records = selectConditionStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.graduationDesignReleaseId).and(a))
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            records = selectConditionStep.fetch()
        }

        for (r in records) {
            val temp = GraduationDesignTeacherBean()
            temp.graduationDesignTeacherId = r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID)
            temp.graduationDesignReleaseId = r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID)
            temp.staffId = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_ID)
            temp.studentCount = r.getValue(GRADUATION_DESIGN_TEACHER.STUDENT_COUNT)
            temp.username = r.getValue(GRADUATION_DESIGN_TEACHER.USERNAME)
            temp.staffRealName = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME)
            temp.staffNumber = r.getValue(STAFF.STAFF_NUMBER)
            temp.staffUsername = r.getValue(STAFF.USERNAME)
            temp.assignerName = r.getValue(GRADUATION_DESIGN_TEACHER.ASSIGNER_NAME)
            graduationDesignTeacherBeens.add(temp)
        }

        return graduationDesignTeacherBeens
    }

    override fun findById(id: String): GraduationDesignTeacher {
        return graduationDesignTeacherDao.findById(id)
    }

    override fun findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId: String, staffId: Int): Optional<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_TEACHER)
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId).and(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(staffId)))
                .fetchOptional()
    }

    override fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): List<GraduationDesignTeacher> {
        return graduationDesignTeacherDao.fetchByGraduationDesignReleaseId(graduationDesignReleaseId)
    }

    override fun findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId: String): List<GraduationDesignTeacherBean> {
        val graduationDesignTeacherBeens = ArrayList<GraduationDesignTeacherBean>()
        val records = create.select()
                .from(GRADUATION_DESIGN_TEACHER)
                .join(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .fetch()

        for (r in records) {
            val temp = GraduationDesignTeacherBean()
            temp.graduationDesignTeacherId = r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID)
            temp.graduationDesignReleaseId = r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID)
            temp.staffId = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_ID)
            temp.studentCount = r.getValue(GRADUATION_DESIGN_TEACHER.STUDENT_COUNT)
            temp.username = r.getValue(GRADUATION_DESIGN_TEACHER.USERNAME)
            temp.staffRealName = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME)
            temp.staffNumber = r.getValue(STAFF.STAFF_NUMBER)
            temp.staffMobile = r.getValue(USERS.MOBILE)
            temp.residue = r.getValue(GRADUATION_DESIGN_TEACHER.RESIDUE)
            graduationDesignTeacherBeens.add(temp)
        }

        return graduationDesignTeacherBeens
    }

    override fun deleteByGraduationDesignReleaseId(graduationDesignReleaseId: String) {
        create.deleteFrom<GraduationDesignTeacherRecord>(GRADUATION_DESIGN_TEACHER).where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .execute()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationDesignTeacher: GraduationDesignTeacher) {
        graduationDesignTeacherDao.insert(graduationDesignTeacher)
    }

    override fun update(graduationDesignTeacher: GraduationDesignTeacher) {
        graduationDesignTeacherDao.update(graduationDesignTeacher)
    }

    override fun update(graduationDesignTeachers: List<GraduationDesignTeacher>) {
        graduationDesignTeacherDao.update(graduationDesignTeachers)
    }

    override fun countAll(graduationDesignTeacherBean: GraduationDesignTeacherBean): Int {
        return create.selectCount()
                .from(GRADUATION_DESIGN_TEACHER)
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.graduationDesignReleaseId))
                .fetchOne().value1()
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignTeacherBean>, graduationDesignTeacherBean: GraduationDesignTeacherBean): Int {
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.graduationDesignReleaseId))
            selectConditionStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.graduationDesignReleaseId).and(a))
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    /**
     * 数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<GraduationDesignTeacherBean>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val realName = StringUtils.trimWhitespace(search!!.getString("realName"))
            val staffUsername = StringUtils.trimWhitespace(search.getString("staffUsername"))
            val staffNumber = StringUtils.trimWhitespace(search.getString("staffNumber"))
            if (StringUtils.hasLength(realName)) {
                a = GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.like(SQLQueryUtils.likeAllParam(realName))
            }

            if (StringUtils.hasLength(staffUsername)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STAFF.USERNAME.like(SQLQueryUtils.likeAllParam(staffUsername))
                } else {
                    a!!.and(STAFF.USERNAME.like(SQLQueryUtils.likeAllParam(staffUsername)))
                }
            }

            if (StringUtils.hasLength(staffNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber))
                } else {
                    a!!.and(STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber)))
                }
            }
        }
        return a
    }

    /**
     * 数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<GraduationDesignTeacherBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("real_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("staff_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = STAFF.STAFF_NUMBER.asc()
                } else {
                    sortField[0] = STAFF.STAFF_NUMBER.desc()
                }
            }

            if ("staff_username".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = STAFF.USERNAME.asc()
                } else {
                    sortField[0] = STAFF.USERNAME.desc()
                }
            }

            if ("student_count".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STUDENT_COUNT.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STUDENT_COUNT.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("assigner_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.ASSIGNER_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.ASSIGNER_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }
        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }

}