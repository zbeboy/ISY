package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.InternshipApply
import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean
import top.zbeboy.isy.web.util.PaginationUtils
import java.sql.Timestamp
import java.util.*

/**
 * Created by zbeboy 2017-12-27 .
 **/
interface InternshipApplyService {
    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 实习申请
     */
    fun findById(id: String): InternshipApply

    /**
     * 通过实习id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Optional<Record>

    /**
     * 保存
     *
     * @param internshipApply 实习申请
     */
    fun save(internshipApply: InternshipApply)

    /**
     * 更新
     *
     * @param internshipApply 实习申请
     */
    fun update(internshipApply: InternshipApply)

    /**
     * 通过实习发布id与申请状态更新状态 定时任务
     *
     * @param internshipReleaseId 实习发布id
     * @param changeState         当前状态
     * @param updateState         新状态
     */
    fun updateStateWithInternshipReleaseIdAndState(internshipReleaseId: String, changeState: Int, updateState: Int)

    /**
     * 更改超过信息填写时间的申请状态为申请中
     *
     * @param changeFillEndTime 填写结束时间
     * @param changeState       当前状态
     * @param updateState       新状态
     */
    fun updateStateByChangeFillEndTime(changeFillEndTime: Timestamp, changeState: Int, updateState: Int)

    /**
     * 通过实习id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    fun deleteByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int)

    /**
     * 删除实习相关记录
     *
     * @param internshipTypeId    实习类型id
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    fun deleteInternshipApplyRecord(internshipTypeId: Int, internshipReleaseId: String, studentId: Int)

    /**
     * 分页查询全部
     *
     * @param paginationUtils     分页工具
     * @param internshipApplyBean 额外参数
     * @return 分页数据
     */
    fun findAllByPage(paginationUtils: PaginationUtils, internshipApplyBean: InternshipApplyBean): Result<Record>

    /**
     * 处理实习返回数据
     *
     * @param paginationUtils     分页工具
     * @param records             数据
     * @param internshipApplyBean 额外参数
     * @return 处理后的数据
     */
    fun dealData(paginationUtils: PaginationUtils, records: Result<Record>, internshipApplyBean: InternshipApplyBean): List<InternshipApplyBean>

    /**
     * 根据条件统计
     *
     * @param paginationUtils     分页工具
     * @param internshipApplyBean 额外参数
     * @return 统计
     */
    fun countByCondition(paginationUtils: PaginationUtils, internshipApplyBean: InternshipApplyBean): Int
}