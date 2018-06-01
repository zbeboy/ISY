package top.zbeboy.isy.service.data

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.tables.daos.NationDao
import top.zbeboy.isy.domain.tables.pojos.Nation
import top.zbeboy.isy.domain.tables.records.NationRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import javax.annotation.Resource

import top.zbeboy.isy.domain.Tables.NATION
/**
 * Created by zbeboy 2017-12-12 .
 **/
@Service("nationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class NationServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<Nation>(), NationService{

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var nationDao: NationDao


    override fun findAll(): List<Nation> {
        return nationDao.findAll()
    }

    override fun findById(id: Int): Nation {
        return nationDao.findById(id)
    }

    override fun findByNationName(nationName: String): List<Nation> {
        return nationDao.fetchByNationName(nationName)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(nation: Nation) {
        nationDao.insert(nation)
    }

    override fun update(nation: Nation) {
        nationDao.update(nation)
    }

    override fun findByNationNameNeNationId(nationName: String, nationId: Int): Result<NationRecord> {
        return create.selectFrom<NationRecord>(NATION)
                .where(NATION.NATION_NAME.eq(nationName).and(NATION.NATION_ID.ne(nationId)))
                .fetch()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<Nation>): Result<Record> {
        return dataPagingQueryAll(dataTablesUtils, create, NATION)
    }

    override fun countAll(): Int {
        return statisticsAll(create, NATION)
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<Nation>): Int {
        return statisticsWithCondition(dataTablesUtils, create, NATION)
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<Nation>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val nationName = StringUtils.trimWhitespace(search!!.getString("nationName"))
            if (StringUtils.hasLength(nationName)) {
                a = NATION.NATION_NAME.like(SQLQueryUtils.likeAllParam(nationName))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<Nation>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("nation_id".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = NATION.NATION_ID.asc()
                } else {
                    sortField[0] = NATION.NATION_ID.desc()
                }
            }

            if ("nation_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = NATION.NATION_NAME.asc()
                } else {
                    sortField[0] = NATION.NATION_NAME.desc()
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}