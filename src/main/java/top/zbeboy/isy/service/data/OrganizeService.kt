package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Record1
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.Organize
import top.zbeboy.isy.domain.tables.records.OrganizeRecord
import top.zbeboy.isy.elastic.pojo.OrganizeElastic
import top.zbeboy.isy.web.bean.data.organize.OrganizeBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-12-03 .
 **/
interface OrganizeService {
    /**
     * 根据专业id，状态查询全部年级
     *
     * @param scienceId 专业id
     * @param b         状态
     * @return 专业下全部年级
     */
    fun findByScienceIdAndDistinctGradeAndIsDel(scienceId: Int, b: Byte?): Result<Record1<String>>

    /**
     * 根据专业ids，年级，状态查询
     *
     * @param scienceIds 专业ids
     * @param grade      年级
     * @param b          班级状态
     * @return 班级
     */
    fun findInScienceIdsAndGradeAndIsDel(scienceIds: List<Int>, grade: String, b: Byte?): Result<OrganizeRecord>

    /**
     * 根据专业id，年级，状态关联查询
     *
     * @param scienceId 专业id
     * @param grade     年级
     * @param b         班级状态
     * @return 班级
     */
    fun findByScienceIdAndGradeAndIsDel(scienceId: Int, grade: String, b: Byte?): Result<OrganizeRecord>

    /**
     * 通过专业查询
     *
     * @param scienceId 专业id
     * @return 班级
     */
    fun findByScienceId(scienceId: Int): List<Organize>

    /**
     * 根据系id查询全部年级
     *
     * @param departmentId 系id
     * @return 系下全部年级
     */
    fun findByDepartmentIdAndDistinctGrade(departmentId: Int): Result<Record1<String>>

    /**
     * 查找专业下不等于该班级id的班级名
     *
     * @param organizeName 班级名
     * @param organizeId   班级id
     * @param scienceId    专业id
     * @return 数据
     */
    fun findByOrganizeNameAndScienceIdNeOrganizeId(organizeName: String, organizeId: Int, scienceId: Int): Result<OrganizeRecord>

    /**
     * 根据年级查询全部班级 注：默认状态为 未注销
     *
     * @param grade     年级
     * @param scienceId 专业id
     * @return 年级下全部班级
     */
    fun findByGradeAndScienceId(grade: String, scienceId: Int): Result<OrganizeRecord>

    /**
     * 根据年级查询全部班级 注：不带状态，用于搜索选择用
     *
     * @param grade     年级
     * @param scienceId 专业id
     * @return 年级下全部班级
     */
    fun findByGradeAndScienceIdNotIsDel(grade: String, scienceId: Int): Result<OrganizeRecord>

    /**
     * 保存
     *
     * @param organizeElastic 班级
     */
    fun save(organizeElastic: OrganizeElastic)

    /**
     * 更新
     *
     * @param organize 班级
     */
    fun update(organize: Organize)

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel is_del
     */
    fun updateIsDel(ids: List<Int>, isDel: Byte?)

    /**
     * 通过id关联查询班级
     *
     * @param id 班级id
     * @return 班级
     */
    fun findByIdRelation(id: Int): Optional<Record>

    /**
     * 通过id查询班级
     *
     * @param id 班级id
     * @return 班级
     */
    fun findById(id: Int): Organize

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<OrganizeBean>): Result<Record>

    /**
     * 班级总数
     *
     * @return 总数
     */
    fun countAll(): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<OrganizeBean>): Int

    /**
     * 专业下 班级名查询 注：等于班级名
     *
     * @param organizeName 班级名
     * @param scienceId    专业id
     * @return 数据
     */
    fun findByOrganizeNameAndScienceId(organizeName: String, scienceId: Int): Result<OrganizeRecord>
}