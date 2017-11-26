package top.zbeboy.isy.service.system

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.APPLICATION
import top.zbeboy.isy.domain.Tables.COLLEGE_APPLICATION
import top.zbeboy.isy.domain.tables.daos.ApplicationDao
import top.zbeboy.isy.domain.tables.pojos.Application
import top.zbeboy.isy.domain.tables.records.ApplicationRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.system.application.ApplicationBean
import top.zbeboy.isy.web.bean.tree.TreeBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-17 .
 **/
@Service("applicationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class ApplicationServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<ApplicationBean>(), ApplicationService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var applicationDao: ApplicationDao

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(application: Application) {
        applicationDao.insert(application)
    }

    override fun update(application: Application) {
        applicationDao.update(application)
    }

    override fun deletes(ids: List<String>) {
        applicationDao.deleteById(ids)
    }

    override fun findById(id: String): Application {
        return applicationDao.findById(id)
    }

    override fun findByPid(pid: String): List<Application> {
        var applications: List<Application> = ArrayList()
        val applicationRecords = create.selectFrom<ApplicationRecord>(APPLICATION)
                .where(APPLICATION.APPLICATION_PID.eq(pid))
                .orderBy(APPLICATION.APPLICATION_SORT.asc())
                .fetch()
        if (applicationRecords.isNotEmpty) {
            applications = applicationRecords.into(Application::class.java)
        }
        return applications
    }

    override fun findByPidAndCollegeId(pid: String, collegeId: Int): List<Application> {
        var applications: List<Application> = ArrayList()
        val records = create.select()
                .from(APPLICATION)
                .join(COLLEGE_APPLICATION)
                .on(APPLICATION.APPLICATION_ID.eq(COLLEGE_APPLICATION.APPLICATION_ID))
                .where(APPLICATION.APPLICATION_PID.eq(pid).and(COLLEGE_APPLICATION.COLLEGE_ID.eq(collegeId)))
                .orderBy(APPLICATION.APPLICATION_SORT.asc())
                .fetch()
        if (records.isNotEmpty) {
            applications = records.into(Application::class.java)
        }
        return applications
    }

    override fun findInPids(pids: List<String>): Result<ApplicationRecord> {
        return create.selectFrom<ApplicationRecord>(APPLICATION)
                .where(APPLICATION.APPLICATION_PID.`in`(pids))
                .fetch()
    }

    override fun findInIdsAndPid(ids: List<String>, pid: String): Result<ApplicationRecord> {
        return create.selectFrom<ApplicationRecord>(APPLICATION)
                .where(APPLICATION.APPLICATION_ID.`in`(ids).and(APPLICATION.APPLICATION_PID.eq(pid)))
                .orderBy(APPLICATION.APPLICATION_SORT)
                .fetch()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<ApplicationBean>): Result<Record> {
        return dataPagingQueryAll(dataTablesUtils, create, APPLICATION)
    }

    override fun countAll(): Int {
        return statisticsAll(create, APPLICATION)
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<ApplicationBean>): Int {
        return statisticsWithCondition(dataTablesUtils, create, APPLICATION)
    }

    override fun findByApplicationName(applicationName: String): List<Application> {
        return applicationDao.fetchByApplicationName(applicationName)
    }

    override fun findByApplicationNameNeApplicationId(applicationName: String, applicationId: String): Result<ApplicationRecord> {
        return create.selectFrom<ApplicationRecord>(APPLICATION)
                .where(APPLICATION.APPLICATION_NAME.eq(applicationName).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch()
    }

    override fun findByApplicationEnName(applicationEnName: String): List<Application> {
        return applicationDao.fetchByApplicationEnName(applicationEnName)
    }

    override fun findByApplicationEnNameNeApplicationId(applicationEnName: String, applicationId: String): Result<ApplicationRecord> {
        return create.selectFrom<ApplicationRecord>(APPLICATION)
                .where(APPLICATION.APPLICATION_EN_NAME.eq(applicationEnName).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch()
    }

    override fun findByApplicationUrl(applicationUrl: String): List<Application> {
        return applicationDao.fetchByApplicationUrl(applicationUrl)
    }

    override fun findByApplicationUrlNeApplicationId(applicationUrl: String, applicationId: String): Result<ApplicationRecord> {
        return create.selectFrom<ApplicationRecord>(APPLICATION)
                .where(APPLICATION.APPLICATION_URL.eq(applicationUrl).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch()
    }

    override fun findByApplicationCode(applicationCode: String): List<Application> {
        return applicationDao.fetchByApplicationCode(applicationCode)
    }

    override fun findByApplicationCodeNeApplicationId(applicationCode: String, applicationId: String): Result<ApplicationRecord> {
        return create.selectFrom<ApplicationRecord>(APPLICATION)
                .where(APPLICATION.APPLICATION_CODE.eq(applicationCode).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch()
    }

    override fun getApplicationJson(pid: String): List<TreeBean> {
        return bindingDataToJson(pid)
    }

    override fun getApplicationJsonByCollegeId(pid: String, collegeId: Int): List<TreeBean> {
        return bindingDataToJson(pid, collegeId)
    }

    /**
     * 绑定数据到treeBean
     *
     * @param id 父id
     * @return list treeBean
     */
    private fun bindingDataToJson(id: String): List<TreeBean> {
        val applications = findByPid(id)
        val treeBeens: MutableList<TreeBean> = ArrayList()
        if (!ObjectUtils.isEmpty(applications)) {
            applications
                    .map {
                        // pid = 0
                        TreeBean(it.applicationName, bindingDataToJson(it.applicationId), it.applicationId)
                    }
                    .forEach { treeBeens.add(it) }
        }
        return treeBeens
    }

    /**
     * 绑定数据到treeBean
     *
     * @param id        父id
     * @param collegeId 院id
     * @return list treeBean
     */
    private fun bindingDataToJson(id: String, collegeId: Int): List<TreeBean> {
        val applications = findByPidAndCollegeId(id, collegeId)
        val treeBeens: MutableList<TreeBean> = ArrayList()
        if (!ObjectUtils.isEmpty(applications)) {
            applications
                    .map {
                        // pid = 0
                        TreeBean(it.applicationName, bindingDataToJson(it.applicationId, collegeId), it.applicationId)
                    }
                    .forEach { treeBeens.add(it) }
        }
        return treeBeens
    }


    /**
     * 应用数据全局搜索条件
     *
     * @param dataTablesUtils datatable工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<ApplicationBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val applicationName = StringUtils.trimWhitespace(search!!.getString("applicationName"))
            val applicationEnName = StringUtils.trimWhitespace(search.getString("applicationEnName"))
            val applicationCode = StringUtils.trimWhitespace(search.getString("applicationCode"))
            if (StringUtils.hasLength(applicationName)) {
                a = APPLICATION.APPLICATION_NAME.like(SQLQueryUtils.likeAllParam(applicationName))
            }

            if (StringUtils.hasLength(applicationEnName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    APPLICATION.APPLICATION_EN_NAME.like(SQLQueryUtils.likeAllParam(applicationEnName))
                } else {
                    a!!.and(APPLICATION.APPLICATION_EN_NAME.like(SQLQueryUtils.likeAllParam(applicationEnName)))
                }
            }

            if (StringUtils.hasLength(applicationCode)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    APPLICATION.APPLICATION_CODE.like(SQLQueryUtils.likeAllParam(applicationCode))
                } else {
                    a!!.and(APPLICATION.APPLICATION_CODE.like(SQLQueryUtils.likeAllParam(applicationCode)))
                }
            }

        }
        return a
    }

    /**
     * 应用数据排序
     *
     * @param dataTablesUtils     datatable工具类
     * @param selectConditionStep 条件
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<ApplicationBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("application_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_NAME.asc()
                } else {
                    sortField[0] = APPLICATION.APPLICATION_NAME.desc()
                }
            }

            if ("application_en_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_EN_NAME.asc()
                } else {
                    sortField[0] = APPLICATION.APPLICATION_EN_NAME.desc()
                }
            }

            if ("application_pid".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_PID.asc()
                    sortField[1] = APPLICATION.APPLICATION_ID.asc()
                } else {
                    sortField[0] = APPLICATION.APPLICATION_PID.desc()
                    sortField[1] = APPLICATION.APPLICATION_ID.desc()
                }
            }

            if ("application_url".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_URL.asc()
                } else {
                    sortField[0] = APPLICATION.APPLICATION_URL.desc()
                }
            }

            if ("icon".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = APPLICATION.ICON.asc()
                    sortField[1] = APPLICATION.APPLICATION_ID.asc()
                } else {
                    sortField[0] = APPLICATION.ICON.desc()
                    sortField[1] = APPLICATION.APPLICATION_ID.desc()
                }
            }

            if ("application_sort".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_SORT.asc()
                } else {
                    sortField[0] = APPLICATION.APPLICATION_SORT.desc()
                }
            }

            if ("application_code".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_CODE.asc()
                } else {
                    sortField[0] = APPLICATION.APPLICATION_CODE.desc()
                }
            }

            if ("application_data_url_start_with".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_DATA_URL_START_WITH.asc()
                } else {
                    sortField[0] = APPLICATION.APPLICATION_DATA_URL_START_WITH.desc()
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }

}