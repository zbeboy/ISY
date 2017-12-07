package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.Building
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease
import top.zbeboy.isy.domain.tables.records.BuildingRecord
import top.zbeboy.isy.web.bean.data.building.BuildingBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-12-07 .
 **/
interface BuildingService {
    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 楼
     */
    fun findById(id: Int): Building

    /**
     * 通过id关联查询楼
     *
     * @param id 楼id
     * @return 楼
     */
    fun findByIdRelation(id: Int): Optional<Record>

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<BuildingBean>): Result<Record>

    /**
     * 根据楼名与院id查询
     *
     * @param buildingName 楼名
     * @param collegeId    院
     * @return 数据
     */
    fun findByBuildingNameAndCollegeId(buildingName: String, collegeId: Int): Result<BuildingRecord>

    /**
     * 根据院id和状态查询全部楼
     *
     * @param collegeId 院id
     * @param isDel     状态
     * @return 数据
     */
    fun findByCollegeIdAndIsDel(collegeId: Int, isDel: Byte?): Result<BuildingRecord>

    /**
     * 查找院下不等于该楼id的楼名
     *
     * @param buildingName 楼名
     * @param collegeId    院id
     * @param buildingId   楼id
     * @return 数据
     */
    fun findByBuildingNameAndCollegeIdNeBuildingId(buildingName: String, collegeId: Int, buildingId: Int): Result<BuildingRecord>

    /**
     * 总数
     *
     * @return 总数
     */
    fun countAll(): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<BuildingBean>): Int

    /**
     * 保存
     *
     * @param building 数据
     */
    fun save(building: Building)

    /**
     * 更新
     *
     * @param building 数据
     */
    fun update(building: Building)

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel is_del
     */
    fun updateIsDel(ids: List<Int>, isDel: Byte?)

    /**
     * 通过毕业设计发布 生成楼数据
     *
     * @param graduationDesignRelease 毕业设计发布
     * @return 楼
     */
    fun generateBuildFromGraduationDesignRelease(graduationDesignRelease: GraduationDesignRelease): List<Building>
}