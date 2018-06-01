package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.Department
import top.zbeboy.isy.domain.tables.records.DepartmentRecord
import top.zbeboy.isy.web.bean.data.department.DepartmentBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-12-02 .
 **/
interface DepartmentService {
    /**
     * 通过院id查询全部系
     *
     * @param collegeId 院id
     * @param b         状态
     * @return 院下全部系
     */
    fun findByCollegeIdAndIsDel(collegeId: Int, b: Byte?): Result<DepartmentRecord>

    /**
     * 保存
     *
     * @param department 系
     */
    fun save(department: Department)

    /**
     * 更新
     *
     * @param department 系
     */
    fun update(department: Department)

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel is_del
     */
    fun updateIsDel(ids: List<Int>, isDel: Byte?)

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<DepartmentBean>): Result<Record>

    /**
     * 系总数
     *
     * @return 总数
     */
    fun countAll(): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<DepartmentBean>): Int

    /**
     * 院下 系名查询 注：等于系名
     *
     * @param departmentName 系名
     * @param collegeId      院id
     * @return 数据
     */
    fun findByDepartmentNameAndCollegeId(departmentName: String, collegeId: Int): Result<DepartmentRecord>

    /**
     * 查找院下不等于该系id的系名
     *
     * @param departmentName 系名
     * @param departmentId   系id
     * @param collegeId      院id
     * @return 数据
     */
    fun findByDepartmentNameAndCollegeIdNeDepartmentId(departmentName: String, departmentId: Int, collegeId: Int): Result<DepartmentRecord>

    /**
     * 通过id关联查询系
     *
     * @param id 系id
     * @return 系
     */
    fun findByIdRelation(id: Int): Optional<Record>

    /**
     * 通过id查询系
     *
     * @param id 系id
     * @return 系
     */
    fun findById(id: Int): Department
}