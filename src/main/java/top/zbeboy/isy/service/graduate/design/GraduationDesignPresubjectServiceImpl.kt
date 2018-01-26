package top.zbeboy.isy.service.graduate.design

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.GraduationDesignPresubjectDao
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPresubject
import top.zbeboy.isy.domain.tables.records.GraduationDesignPresubjectRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.graduate.design.subject.GraduationDesignPresubjectBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-26 .
 **/
@Service("graduationDesignPresubjectService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignPresubjectServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<GraduationDesignPresubjectBean>(), GraduationDesignPresubjectService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var graduationDesignPresubjectDao: GraduationDesignPresubjectDao


    override fun findById(id: String): GraduationDesignPresubject {
        return graduationDesignPresubjectDao.findById(id)
    }

    override fun findByPresubjectTitle(presubjectTitle: String): List<GraduationDesignPresubject> {
        return graduationDesignPresubjectDao.fetchByPresubjectTitle(presubjectTitle)
    }

    override fun findByPresubjectTitleNeId(presubjectTitle: String, graduationDesignPresubjectId: String): Result<GraduationDesignPresubjectRecord> {
        return create.selectFrom(GRADUATION_DESIGN_PRESUBJECT)
                .where(GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.eq(presubjectTitle).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.ne(graduationDesignPresubjectId)))
                .fetch()
    }

    override fun findByIdRelation(id: String): Optional<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_PRESUBJECT)
                .leftJoin(GRADUATION_DESIGN_DECLARE)
                .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                .where(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(id))
                .fetchOptional()
    }

    override fun findByStudentIdAndGraduationDesignReleaseId(studentId: Int, graduationDesignReleaseId: String): GraduationDesignPresubjectRecord {
        return create.selectFrom(GRADUATION_DESIGN_PRESUBJECT)
                .where(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetchOne()
    }

    override fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): Result<GraduationDesignPresubjectRecord> {
        return create.selectFrom(GRADUATION_DESIGN_PRESUBJECT)
                .where(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationDesignPresubject: GraduationDesignPresubject) {
        graduationDesignPresubjectDao.insert(graduationDesignPresubject)
    }

    override fun update(graduationDesignPresubject: GraduationDesignPresubject) {
        graduationDesignPresubjectDao.update(graduationDesignPresubject)
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignPresubjectBean>, graduationDesignPresubjectBean: GraduationDesignPresubjectBean): Result<Record> {
        val a = searchCondition(dataTablesUtils)
        return if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_PRESUBJECT)
                    .join(STUDENT)
                    .on(STUDENT.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignPresubjectBean.graduationDesignReleaseId))
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_PRESUBJECT)
                    .join(STUDENT)
                    .on(STUDENT.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignPresubjectBean.graduationDesignReleaseId).and(a))
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        }
    }

    override fun countAll(graduationDesignPresubjectBean: GraduationDesignPresubjectBean): Int {
        return create.selectCount()
                .from(GRADUATION_DESIGN_PRESUBJECT)
                .where(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignPresubjectBean.graduationDesignReleaseId))
                .fetchOne().value1()
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignPresubjectBean>, graduationDesignPresubjectBean: GraduationDesignPresubjectBean): Int {
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_PRESUBJECT)
                    .where(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignPresubjectBean.graduationDesignReleaseId))
            selectConditionStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_PRESUBJECT)
                    .join(STUDENT)
                    .on(STUDENT.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignPresubjectBean.graduationDesignReleaseId).and(a))
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
    override fun searchCondition(dataTablesUtils: DataTablesUtils<GraduationDesignPresubjectBean>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val presubjectTitle = StringUtils.trimWhitespace(search!!.getString("presubjectTitle"))
            val studentName = StringUtils.trimWhitespace(search.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val organize = StringUtils.trimWhitespace(search.getString("organize"))
            if (StringUtils.hasLength(presubjectTitle)) {
                a = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.like(SQLQueryUtils.likeAllParam(presubjectTitle))
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

            if (StringUtils.hasLength(organize)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organize))
                } else {
                    a!!.and(ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organize)))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<GraduationDesignPresubjectBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("presubject_title".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.asc()
                    sortField[1] = GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.desc()
                    sortField[1] = GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.desc()
                }
            }

            if ("student_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.asc()
                } else {
                    sortField[0] = USERS.REAL_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.desc()
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
                    sortField[1] = GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.asc()
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.desc()
                }
            }

            if ("update_time".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.UPDATE_TIME.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.UPDATE_TIME.desc()
                }
            }
        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}