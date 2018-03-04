package top.zbeboy.isy.service.graduate.design

import org.apache.commons.lang.math.NumberUtils
import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.tables.daos.GraduationDesignDatumDao
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumBean
import javax.annotation.Resource

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatum
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2018-01-29 .
 **/
@Service("graduationDesignDatumService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignDatumServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<GraduationDesignDatumBean>(), GraduationDesignDatumService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var graduationDesignDatumDao: GraduationDesignDatumDao

    override fun findById(id: String): GraduationDesignDatum {
        return graduationDesignDatumDao.findById(id)
    }

    override fun findByIdRelation(id: String): Optional<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_DATUM)
                .join(FILES)
                .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                .join(GRADUATION_DESIGN_TUTOR)
                .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.eq(id))
                .fetchOptional()
    }

    override fun findByGraduationDesignTutorIdAndGraduationDesignDatumTypeId(graduationDesignTutorId: String, graduationDesignDatumTypeId: Int): Optional<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_DATUM)
                .join(GRADUATION_DESIGN_TUTOR)
                .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                .where(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(graduationDesignTutorId).and(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(graduationDesignDatumTypeId)))
                .fetchOptional()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignDatumBean>, graduationDesignDatumBean: GraduationDesignDatumBean): Result<Record> {
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduationDesignDatumBean)
        return if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
            sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .where(a)
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        }
    }

    override fun countAll(graduationDesignDatumBean: GraduationDesignDatumBean): Int {
        val count: Record1<Int>
        val a = otherCondition(null, graduationDesignDatumBean)
        count = if (ObjectUtils.isEmpty(a)) {
            create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .fetchOne()
        } else {
            create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .where(a)
                    .fetchOne()
        }
        return count.value1()
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDatumBean>, graduationDesignDatumBean: GraduationDesignDatumBean): Int {
        val count: Record1<Int>
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduationDesignDatumBean)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
            selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .where(a)
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    override fun findTeamAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignDatumBean>, graduationDesignDatumBean: GraduationDesignDatumBean): Result<Record> {
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduationDesignDatumBean)
        return if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
            sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(a)
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        }
    }

    override fun countTeamAll(graduationDesignDatumBean: GraduationDesignDatumBean): Int {
        val count: Record1<Int>
        val a = otherCondition(null, graduationDesignDatumBean)
        count = if (ObjectUtils.isEmpty(a)) {
            create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .fetchOne()
        } else {
            create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(a)
                    .fetchOne()
        }
        return count.value1()
    }

    override fun countTeamByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDatumBean>, graduationDesignDatumBean: GraduationDesignDatumBean): Int {
        val count: Record1<Int>
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduationDesignDatumBean)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
            selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(a)
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    override fun update(graduationDesignDatum: GraduationDesignDatum) {
        graduationDesignDatumDao.update(graduationDesignDatum)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationDesignDatum: GraduationDesignDatum) {
        graduationDesignDatumDao.insert(graduationDesignDatum)
    }

    override fun deleteById(id: String) {
        graduationDesignDatumDao.deleteById(id)
    }

    /**
     * 其它条件
     *
     * @param graduationDesignDatumBean 条件
     * @return 条件
     */
    fun otherCondition(a: Condition?, graduationDesignDatumBean: GraduationDesignDatumBean): Condition? {
        var tempCondition = a
        if (!ObjectUtils.isEmpty(graduationDesignDatumBean)) {
            if (StringUtils.hasLength(graduationDesignDatumBean.graduationDesignTutorId)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(graduationDesignDatumBean.graduationDesignTutorId))
                } else {
                    GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(graduationDesignDatumBean.graduationDesignTutorId)
                }
            }

            if (StringUtils.hasLength(graduationDesignDatumBean.graduationDesignReleaseId)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDatumBean.graduationDesignReleaseId))
                } else {
                    GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDatumBean.graduationDesignReleaseId)
                }
            }

            if (!ObjectUtils.isEmpty(graduationDesignDatumBean.staffId) && graduationDesignDatumBean.staffId > 0) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDatumBean.staffId))
                } else {
                    GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDatumBean.staffId)
                }
            }
        }
        return tempCondition
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDatumBean>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val originalFileName = StringUtils.trimWhitespace(search!!.getString("originalFileName"))
            val graduationDesignDatumTypeName = StringUtils.trimWhitespace(search.getString("graduationDesignDatumTypeName"))
            val studentName = StringUtils.trimWhitespace(search.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            if (StringUtils.hasLength(originalFileName)) {
                a = FILES.ORIGINAL_FILE_NAME.like(SQLQueryUtils.likeAllParam(originalFileName))
            }

            if (StringUtils.hasLength(graduationDesignDatumTypeName)) {
                val graduationDesignDatumTypeId = NumberUtils.toInt(graduationDesignDatumTypeName)
                if (graduationDesignDatumTypeId > 0) {
                    a = if (ObjectUtils.isEmpty(a)) {
                        GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(graduationDesignDatumTypeId)
                    } else {
                        a!!.and(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(graduationDesignDatumTypeId))
                    }
                }
            }

            if (StringUtils.hasLength(studentName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName))
                } else {
                    a!!.and(USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName)))
                }
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDatumBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {

            if ("real_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.asc()
                } else {
                    sortField[0] = USERS.REAL_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.desc()
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
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.asc()
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.desc()
                }
            }

            if ("original_file_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = FILES.ORIGINAL_FILE_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.desc()
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.desc()
                }
            }

            if ("graduation_design_datum_type_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.asc()
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.desc()
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.desc()
                }
            }

            if ("version".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DATUM.VERSION.asc()
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DATUM.VERSION.desc()
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.desc()
                }
            }

            if ("update_time".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DATUM.UPDATE_TIME.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DATUM.UPDATE_TIME.desc()
                }
            }
        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}