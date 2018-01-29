package top.zbeboy.isy.service.graduate.design

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.tables.daos.GraduationDesignDatumGroupDao
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumGroupBean
import javax.annotation.Resource

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2018-01-29 .
 **/
@Service("graduationDesignDatumGroupService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignDatumGroupServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<GraduationDesignDatumGroupBean>(), GraduationDesignDatumGroupService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var graduationDesignDatumGroupDao: GraduationDesignDatumGroupDao

    override fun findById(id: String): GraduationDesignDatumGroup {
        return graduationDesignDatumGroupDao.findById(id)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationDesignDatumGroup: GraduationDesignDatumGroup) {
        graduationDesignDatumGroupDao.insert(graduationDesignDatumGroup)
    }

    override fun delete(graduationDesignDatumGroup: GraduationDesignDatumGroup) {
        graduationDesignDatumGroupDao.delete(graduationDesignDatumGroup)
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignDatumGroupBean>, graduationDesignDatumGroupBean: GraduationDesignDatumGroupBean): Result<Record> {
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduationDesignDatumGroupBean)
        return if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM_GROUP)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.FILE_ID.eq(FILES.FILE_ID))
            sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM_GROUP)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .where(a)
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        }
    }

    override fun countAll(graduationDesignDatumGroupBean: GraduationDesignDatumGroupBean): Int {
        val count: Record1<Int>
        val a = otherCondition(null, graduationDesignDatumGroupBean)
        count = if (ObjectUtils.isEmpty(a)) {
            create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM_GROUP)
                    .fetchOne()
        } else {
            create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM_GROUP)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .where(a)
                    .fetchOne()
        }
        return count.value1()
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDatumGroupBean>, graduationDesignDatumGroupBean: GraduationDesignDatumGroupBean): Int {
        val count: Record1<Int>
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduationDesignDatumGroupBean)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM_GROUP)
            selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM_GROUP)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .where(a)
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    /**
     * 其它条件
     *
     * @param graduationDesignDatumGroupBean 条件
     * @return 条件
     */
    fun otherCondition(a: Condition?, graduationDesignDatumGroupBean: GraduationDesignDatumGroupBean): Condition? {
        var tempCondition = a
        if (!ObjectUtils.isEmpty(graduationDesignDatumGroupBean)) {

            if (StringUtils.hasLength(graduationDesignDatumGroupBean.graduationDesignReleaseId)) {
                if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition = tempCondition!!.and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDatumGroupBean.graduationDesignReleaseId))
                } else {
                    tempCondition = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDatumGroupBean.graduationDesignReleaseId)
                }
            }

            if (StringUtils.hasLength(graduationDesignDatumGroupBean.graduationDesignTeacherId)) {
                if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition = tempCondition!!.and(GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignDatumGroupBean.graduationDesignTeacherId))
                } else {
                    tempCondition = GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignDatumGroupBean.graduationDesignTeacherId)
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
    override fun searchCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDatumGroupBean>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val originalFileName = StringUtils.trimWhitespace(search!!.getString("originalFileName"))
            if (StringUtils.hasLength(originalFileName)) {
                a = FILES.ORIGINAL_FILE_NAME.like(SQLQueryUtils.likeAllParam(originalFileName))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDatumGroupBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {

            if ("original_file_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = FILES.ORIGINAL_FILE_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_DATUM_GROUP_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.desc()
                    sortField[1] = GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_DATUM_GROUP_ID.desc()
                }
            }

            if ("upload_time".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DATUM_GROUP.UPLOAD_TIME.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DATUM_GROUP.UPLOAD_TIME.desc()
                }
            }
        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}