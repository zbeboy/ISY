package top.zbeboy.isy.service.data

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.tables.daos.AcademicTitleDao
import top.zbeboy.isy.domain.tables.pojos.AcademicTitle
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import javax.annotation.Resource

import top.zbeboy.isy.domain.Tables.ACADEMIC_TITLE
import top.zbeboy.isy.domain.tables.records.AcademicTitleRecord
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-12 .
 **/
@Service("academicTitleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class AcademicTitleServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<AcademicTitle>(), AcademicTitleService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var academicTitleDao: AcademicTitleDao


    override fun findAll(): List<AcademicTitle> {
        return academicTitleDao.findAll()
    }

    override fun findById(id: Int): AcademicTitle {
        return academicTitleDao.findById(id)
    }

    override fun findByAcademicTitleName(academicTitleName: String): List<AcademicTitle> {
        return academicTitleDao.fetchByAcademicTitleName(academicTitleName)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(academicTitle: AcademicTitle) {
        academicTitleDao.insert(academicTitle)
    }

    override fun update(academicTitle: AcademicTitle) {
        academicTitleDao.update(academicTitle)
    }

    override fun findByAcademicTitleNameNeAcademicTitleId(academicTitleName: String, academicTitleId: Int): Result<AcademicTitleRecord> {
        return create.selectFrom(ACADEMIC_TITLE)
                .where(ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.eq(academicTitleName).and(ACADEMIC_TITLE.ACADEMIC_TITLE_ID.ne(academicTitleId)))
                .fetch()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<AcademicTitle>): Result<Record> {
        return dataPagingQueryAll(dataTablesUtils, create, ACADEMIC_TITLE)
    }

    override fun countAll(): Int {
        return statisticsAll(create, ACADEMIC_TITLE)
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<AcademicTitle>): Int {
        return statisticsWithCondition(dataTablesUtils, create, ACADEMIC_TITLE)
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<AcademicTitle>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val academicTitleName = StringUtils.trimWhitespace(search!!.getString("academicTitleName"))
            if (StringUtils.hasLength(academicTitleName)) {
                a = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.like(SQLQueryUtils.likeAllParam(academicTitleName))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<AcademicTitle>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("academic_title_id".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_ID.asc()
                } else {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_ID.desc()
                }
            }

            if ("academic_title_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.asc()
                } else {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.desc()
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}