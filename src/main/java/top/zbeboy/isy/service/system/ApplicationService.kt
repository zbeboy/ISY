package top.zbeboy.isy.service.system

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.Application
import top.zbeboy.isy.domain.tables.records.ApplicationRecord
import top.zbeboy.isy.web.bean.system.application.ApplicationBean
import top.zbeboy.isy.web.bean.tree.TreeBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-11-17 .
 **/
interface ApplicationService {

    /**
     * 保存
     *
     * @param application 应用
     */
    fun save(application: Application)

    /**
     * 更新
     *
     * @param application 应用
     */
    fun update(application: Application)

    /**
     * 批量删除
     *
     * @param ids ids
     */
    fun deletes(ids: List<String>)

    /**
     * 通过id查询
     *
     * @param id id
     * @return 应用
     */
    fun findById(id: String): Application

    /**
     * 通过pid查询
     *
     * @param pid 父id
     * @return 应用
     */
    fun findByPid(pid: String): List<Application>

    /**
     * 通过pid查询
     *
     * @param pid       父id
     * @param collegeId 院id
     * @return 应用
     */
    fun findByPidAndCollegeId(pid: String, collegeId: Int): List<Application>

    /**
     * 通过pids查询
     *
     * @param pids 父ids
     * @return 应用
     */
    fun findInPids(pids: List<String>): Result<ApplicationRecord>

    /**
     * 通过ids和父id查询
     *
     * @param ids ids
     * @param pid 父id
     * @return 应用
     */
    fun findInIdsAndPid(ids: List<String>, pid: String): Result<ApplicationRecord>

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<ApplicationBean>): Result<Record>

    /**
     * 应用 总数
     *
     * @return 总数
     */
    fun countAll(): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<ApplicationBean>): Int

    /**
     * 通过应用名查询
     *
     * @param applicationName 应用名
     * @return 应用
     */
    fun findByApplicationName(applicationName: String): List<Application>

    /**
     * 通过应用名与应用id查询
     *
     * @param applicationName 应用名
     * @param applicationId   应用id
     * @return 应用
     */
    fun findByApplicationNameNeApplicationId(applicationName: String, applicationId: String): Result<ApplicationRecord>

    /**
     * 通过应用英文名查询
     *
     * @param applicationEnName 应用英文名
     * @return 应用
     */
    fun findByApplicationEnName(applicationEnName: String): List<Application>

    /**
     * 通过应用英文名与应用id查询
     *
     * @param applicationEnName 应用英文名
     * @param applicationId     应用id
     * @return 应用
     */
    fun findByApplicationEnNameNeApplicationId(applicationEnName: String, applicationId: String): Result<ApplicationRecord>

    /**
     * 通过应用链接查询
     *
     * @param applicationUrl 应用链接
     * @return 应用
     */
    fun findByApplicationUrl(applicationUrl: String): List<Application>

    /**
     * 通过应用链接与应用id查询
     *
     * @param applicationUrl 应用链接
     * @param applicationId  应用id
     * @return 应用
     */
    fun findByApplicationUrlNeApplicationId(applicationUrl: String, applicationId: String): Result<ApplicationRecord>

    /**
     * 通过应用识别码查询
     *
     * @param applicationCode 应用识别码
     * @return 应用
     */
    fun findByApplicationCode(applicationCode: String): List<Application>

    /**
     * 通过应用识别码与应用id查询
     *
     * @param applicationCode 应用识别码
     * @param applicationId   应用id
     * @return 应用
     */
    fun findByApplicationCodeNeApplicationId(applicationCode: String, applicationId: String): Result<ApplicationRecord>

    /**
     * 通过父id获取所有子类组成的List
     *
     * @param pid 父id
     * @return list json
     */
    fun getApplicationJson(pid: String): List<TreeBean>

    /**
     * 通过父id与院id获取所有子类组成的List
     *
     * @param pid 父id
     * @return list json
     */
    fun getApplicationJsonByCollegeId(pid: String, collegeId: Int): List<TreeBean>
}