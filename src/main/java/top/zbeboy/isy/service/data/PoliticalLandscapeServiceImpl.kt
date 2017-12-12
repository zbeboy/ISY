package top.zbeboy.isy.service.data

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.tables.daos.PoliticalLandscapeDao
import top.zbeboy.isy.domain.tables.pojos.PoliticalLandscape
import top.zbeboy.isy.domain.tables.records.PoliticalLandscapeRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import javax.annotation.Resource

import top.zbeboy.isy.domain.Tables.POLITICAL_LANDSCAPE
/**
 * Created by zbeboy 2017-12-12 .
 **/
@Service("politicalLandscapeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class PoliticalLandscapeServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<PoliticalLandscape>(),PoliticalLandscapeService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var politicalLandscapeDao: PoliticalLandscapeDao


    override fun findAll(): List<PoliticalLandscape> {
        return politicalLandscapeDao.findAll()
    }

    override fun findById(id: Int): PoliticalLandscape {
        return politicalLandscapeDao.findById(id)
    }

    override fun findByPoliticalLandscapeName(politicalLandscapeName: String): List<PoliticalLandscape> {
        return politicalLandscapeDao.fetchByPoliticalLandscapeName(politicalLandscapeName)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(politicalLandscape: PoliticalLandscape) {
        politicalLandscapeDao.insert(politicalLandscape)
    }

    override fun update(politicalLandscape: PoliticalLandscape) {
        politicalLandscapeDao.update(politicalLandscape)
    }

    override fun findByNationNameNePoliticalLandscapeId(politicalLandscapeName: String, politicalLandscapeId: Int): Result<PoliticalLandscapeRecord> {
        return create.selectFrom<PoliticalLandscapeRecord>(POLITICAL_LANDSCAPE)
                .where(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.eq(politicalLandscapeName).and(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID.ne(politicalLandscapeId)))
                .fetch()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<PoliticalLandscape>): Result<Record> {
        return dataPagingQueryAll(dataTablesUtils, create, POLITICAL_LANDSCAPE)
    }

    override fun countAll(): Int {
        return statisticsAll(create, POLITICAL_LANDSCAPE)
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<PoliticalLandscape>): Int {
        return statisticsWithCondition(dataTablesUtils, create, POLITICAL_LANDSCAPE)
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<PoliticalLandscape>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val politicalLandscapeName = StringUtils.trimWhitespace(search!!.getString("politicalLandscapeName"))
            if (StringUtils.hasLength(politicalLandscapeName)) {
                a = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.like(SQLQueryUtils.likeAllParam(politicalLandscapeName))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<PoliticalLandscape>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("political_landscape_id".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID.asc()
                } else {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID.desc()
                }
            }

            if ("political_landscape_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.asc()
                    sortField[1] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID.asc()
                } else {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.desc()
                    sortField[1] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID.desc()
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}