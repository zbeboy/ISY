package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Record2
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.Science
import top.zbeboy.isy.domain.tables.records.ScienceRecord
import top.zbeboy.isy.web.bean.data.science.ScienceBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-12-03 .
 **/
interface ScienceService {
    /**
     * 通过系id查询全部专业
     *
     * @param departmentId 系id
     * @param b            状态
     * @return 系下全部专业
     */
    fun findByDepartmentIdAndIsDel(departmentId: Int, b: Byte?): Result<ScienceRecord>

    /**
     * 通过年级查询全部专业
     *
     * @param grade        年级
     * @param departmentId 系id
     * @return 年级下全部专业
     */
    fun findByGradeAndDepartmentId(grade: String, departmentId: Int): Result<Record2<String, Int>>

    /**
     * 保存
     *
     * @param science 专业
     */
    fun save(science: Science)

    /**
     * 更新
     *
     * @param science 专业
     */
    fun update(science: Science)

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel is_del
     */
    fun updateIsDel(ids: List<Int>, isDel: Byte?)

    /**
     * 通过id查询
     *
     * @param id 专业id
     * @return 专业
     */
    fun findById(id: Int): Science

    /**
     * 通过id关联查询
     *
     * @param id 专业id
     * @return 专业
     */
    fun findByIdRelation(id: Int): Optional<Record>

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<ScienceBean>): Result<Record>

    /**
     * 专业总数
     *
     * @return 总数
     */
    fun countAll(): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<ScienceBean>): Int

    /**
     * 系下 专业名查询 注：等于专业名
     *
     * @param scienceName  专业名
     * @param departmentId 系id
     * @return 数据
     */
    fun findByScienceNameAndDepartmentId(scienceName: String, departmentId: Int): Result<ScienceRecord>

    /**
     * 专业代码查询 注：等于专业代码
     *
     * @param scienceCode 专业代码
     * @return 数据
     */
    fun findByScienceCode(scienceCode: String): Result<ScienceRecord>

    /**
     * 查找系下不等于该专业id的专业名
     *
     * @param scienceName  专业名
     * @param scienceId    专业id
     * @param departmentId 系id
     * @return 数据
     */
    fun findByScienceNameAndDepartmentIdNeScienceId(scienceName: String, scienceId: Int, departmentId: Int): Result<ScienceRecord>

    /**
     * 专业代码查询 注：等于专业id
     *
     * @param scienceCode 专业代码
     * @param scienceId   专业id
     * @return 数据
     */
    fun findByScienceCodeNeScienceId(scienceCode: String, scienceId: Int): Result<ScienceRecord>
}