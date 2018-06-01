package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.School
import top.zbeboy.isy.domain.tables.records.SchoolRecord
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-01 .
 **/
interface SchoolService {
    /**
     * 查询全部学校
     *
     * @param b 状态
     * @return 全部学校
     */
    fun findByIsDel(b: Byte?): Result<SchoolRecord>

    /**
     * 保存
     *
     * @param school 学校
     */
    fun save(school: School)

    /**
     * 更新
     *
     * @param school 学校
     */
    fun update(school: School)

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<School>): Result<Record>

    /**
     * 学校总数
     *
     * @return 总数
     */
    fun countAll(): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<School>): Int

    /**
     * 根据学校名查询 注：等于学校名
     *
     * @param schoolName 学校名
     * @return 数据
     */
    fun findBySchoolName(schoolName: String): List<School>

    /**
     * 查找不等于该学校id的学校名
     *
     * @param schoolName 学校名
     * @param schoolId   学校id
     * @return 数据
     */
    fun findBySchoolNameNeSchoolId(schoolName: String, schoolId: Int): Result<SchoolRecord>

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel is_del
     */
    fun updateIsDel(ids: List<Int>, isDel: Byte?)

    /**
     * 通过id查询学校
     *
     * @param id id
     * @return 学校
     */
    fun findById(id: Int): School
}