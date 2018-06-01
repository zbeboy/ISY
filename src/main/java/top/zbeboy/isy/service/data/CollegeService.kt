package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.College
import top.zbeboy.isy.domain.tables.records.CollegeRecord
import top.zbeboy.isy.web.bean.data.college.CollegeBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-12-01 .
 **/
interface CollegeService {
    /**
     * 通过学校id查询全部院
     *
     * @param schoolId 学校id
     * @param b        状态
     * @return 该学校下的全部院
     */
    fun findBySchoolIdAndIsDel(schoolId: Int, b: Byte?): Result<CollegeRecord>

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<CollegeBean>): Result<Record>

    /**
     * 院总数
     *
     * @return 总数
     */
    fun countAll(): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<CollegeBean>): Int

    /**
     * 学校下 院名查询 注：等于院名
     *
     * @param collegeName 院名
     * @param schoolId    学校id
     * @return 数据
     */
    fun findByCollegeNameAndSchoolId(collegeName: String, schoolId: Int): Result<CollegeRecord>

    /**
     * 院代码查询 注：等于院代码
     *
     * @param collegeCode 院代码
     * @return 数据
     */
    fun findByCollegeCode(collegeCode: String): Result<CollegeRecord>

    /**
     * 保存
     *
     * @param college 院
     */
    fun save(college: College)

    /**
     * 更新
     *
     * @param college 院
     */
    fun update(college: College)

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel 状态
     */
    fun updateIsDel(ids: List<Int>, isDel: Byte?)

    /**
     * 通过id查询院
     *
     * @param id 院id
     * @return 院
     */
    fun findById(id: Int): College

    /**
     * 通过id关联查询
     *
     * @param collegeId 院id
     * @return 数据
     */
    fun findByIdRelation(collegeId: Int): Optional<Record>

    /**
     * 查找学校下不等于该院id的院名
     *
     * @param collegeName 院名
     * @param collegeId   院id
     * @param schoolId    学校id
     * @return 院
     */
    fun findByCollegeNameAndSchoolIdNeCollegeId(collegeName: String, collegeId: Int, schoolId: Int): Result<CollegeRecord>

    /**
     * 学校下 院代码查询 注：不等于院id
     *
     * @param collegeCode 院代码
     * @param collegeId   院id
     * @return 数据
     */
    fun findByCollegeCodeNeCollegeId(collegeCode: String, collegeId: Int): Result<CollegeRecord>
}