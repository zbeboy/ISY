package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.Schoolroom
import top.zbeboy.isy.domain.tables.records.SchoolroomRecord
import top.zbeboy.isy.web.bean.data.schoolroom.SchoolroomBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-12-08 .
 **/
interface SchoolroomService {
    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 教室
     */
    fun findById(id: Int): Schoolroom

    /**
     * 根据主键关联查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findByIdRelation(id: Int): Optional<Record>

    /**
     * 通过教室与楼id查询
     *
     * @param buildingCode 教室
     * @param buildingId   楼id
     * @return 数据
     */
    fun findByBuildingCodeAndBuildingId(buildingCode: String, buildingId: Int): Result<SchoolroomRecord>

    /**
     * 检验一栋里是否有相同教室名
     *
     * @param buildingCode 教室
     * @param schoolroomId 教室id
     * @param buildingId   楼id
     * @return 数据
     */
    fun findByBuildingCodeAndBuildingIdNeSchoolroomId(buildingCode: String, schoolroomId: Int, buildingId: Int): Result<SchoolroomRecord>

    /**
     * 通过楼id与状态查询
     *
     * @param buildingId 楼id
     * @param b          状态
     * @return 数据
     */
    fun findByBuildingIdAndIsDel(buildingId: Int, b: Byte?): Result<SchoolroomRecord>

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<SchoolroomBean>): Result<Record>

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
    fun countByCondition(dataTablesUtils: DataTablesUtils<SchoolroomBean>): Int

    /**
     * 保存
     *
     * @param schoolroom 数据
     */
    fun save(schoolroom: Schoolroom)

    /**
     * 更新
     *
     * @param schoolroom 数据
     */
    fun update(schoolroom: Schoolroom)

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel is_del
     */
    fun updateIsDel(ids: List<Int>, isDel: Byte?)
}